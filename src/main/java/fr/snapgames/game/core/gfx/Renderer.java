package fr.snapgames.game.core.gfx;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.CameraEntity;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.behaviors.Behavior;
import fr.snapgames.game.core.entity.Entity;
import fr.snapgames.game.core.gfx.plugins.GameEntityDrawPlugin;
import fr.snapgames.game.core.gfx.plugins.RendererPlugin;
import fr.snapgames.game.core.gfx.plugins.TextEntityDrawPlugin;
import fr.snapgames.game.core.math.physic.PhysicEngine;
import fr.snapgames.game.core.utils.I18n;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This is where all things are drawn.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class Renderer {
    private final String debugEntityNames;
    Configuration config;
    JFrame frame;
    BufferedImage buffer;
    CameraEntity currentCamera;
    // Internal components
    Color clearColor = Color.BLACK;

    private Map<Class<? extends Entity>, RendererPlugin> plugins = new HashMap<>();
    private Game game;

    public Renderer(Game g) {
        this.game = g;
        config = g.getConfiguration();
        frame = g.getFrame();
        buffer = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
        addPlugin(new GameEntityDrawPlugin());
        addPlugin(new TextEntityDrawPlugin());
        debugEntityNames = config.getString("game.debug.entity.list", "");
    }

    public void addPlugin(RendererPlugin rp) {
        this.plugins.put(rp.entityType(), rp);
    }

    public void draw(Game game, long realFPS) {
        currentCamera = game.getCurrentCamera();
        double scale = config.getDouble("game.screen.scale", 2.0);
        if (Optional.ofNullable(buffer).isPresent()) {
            Graphics2D g = buffer.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            // clear scene
            g.setColor(clearColor);
            g.clearRect(0, 0,
                    game.getWidth(),
                    game.getHeight());
            // draw Scene

            drawPlayAreaGrid(g, 32, game.getPhysicEngine());

            game.getEntities().values().forEach(e -> {
                GameEntity entity = (GameEntity) e;
                preDraw(g, entity.isStickToCamera());
                for (Behavior b : entity.behaviors) {
                    b.draw(game, entity, g);
                }
                drawEntity(g, entity);
                postDraw(g, entity.isStickToCamera());
            });

            displayPauseMessage(g, game.getPause());
            if (game.getDebug() > 0) {
                drawDisplayDebugInfo(game, g, 32);
                if (Optional.ofNullable(currentCamera).isPresent()
                        && game.getDebug() > 1) {
                    drawCameraDebug(g, currentCamera);
                }
                drawScreenDebugLine(g, currentCamera.viewport);
            }
            // draw image to screen.
            drawToWindow(realFPS, scale);

            g.dispose();
        }
    }

    private void displayPauseMessage(Graphics2D g, boolean pause) {
        if (pause) {
            Dimension viewport = currentCamera.viewport;
            g.setColor(new Color(0.3f, 0.6f, 0.4f, 0.9f));
            g.fillRect(0, (viewport.height - 24) / 2, viewport.width, 24);

            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(14.0f).deriveFont(Font.BOLD));

            String pauseTxt = I18n.get("game.state.pause.message");
            int lng = g.getFontMetrics().stringWidth(pauseTxt);

            g.drawString(
                    pauseTxt,
                    (currentCamera.viewport.width - lng) / 2,
                    (currentCamera.viewport.height + 12) / 2);
        }
    }

    private void drawToWindow(double realFPS, double scale) {
        if (Optional.ofNullable(frame).isPresent() && frame.getBufferStrategy() != null) {
            if (frame.getBufferStrategy().getDrawGraphics() == null) {
                return;
            }
            Graphics2D g2 = (Graphics2D) frame.getBufferStrategy().getDrawGraphics();

            g2.scale(scale, scale);
            g2.drawImage(buffer, 0, 18,
                    null);
            g2.scale(1.0 / scale, 1.0 / scale);
            if (game.getDebug() > 1) {
                g2.setColor(Color.ORANGE);

                g2.setFont(g2.getFont().deriveFont(11.0f));
                g2.drawString("FPS:" + realFPS, 40, 50);
            }
            g2.dispose();
            if (frame.getBufferStrategy() != null) {
                frame.getBufferStrategy().show();
            }
        }
    }

    private void drawScreenDebugLine(Graphics2D g, Dimension viewport) {
        g.setColor(new Color(0.3f, 0.0f, 0.0f, 0.7f));
        g.fillRect(0, viewport.height - 20, viewport.width, 20);
        g.setColor(Color.ORANGE);
        String text = String.format("[ dbg:%01d | fps:%03d]", game.getDebug(), game.getRealFPS());
        g.setFont(g.getFont().deriveFont(10.0f));
        g.drawString(text, 8, viewport.height - 8);
    }

    private void preDraw(Graphics2D g, boolean isSticky) {
        if (Optional.ofNullable(currentCamera).isPresent() && !isSticky) {
            if (currentCamera.rotation != 0.0) {
                g.rotate(-currentCamera.rotation, currentCamera.viewport.width * 0.5,
                        currentCamera.viewport.height * 0.5);
            }
            g.translate(-currentCamera.position.x, -currentCamera.position.y);
        }
    }

    private void postDraw(Graphics2D g, boolean isSticky) {
        if (Optional.ofNullable(currentCamera).isPresent() && !isSticky) {
            g.translate(currentCamera.position.x, currentCamera.position.y);
            if (currentCamera.rotation != 0.0) {
                g.rotate(currentCamera.rotation, currentCamera.viewport.width * 0.5,
                        currentCamera.viewport.height * 0.5);
            }
        }
    }

    public void drawEntity(Graphics2D g, Entity e) {
        if (plugins.containsKey(e.getClass())) {
            RendererPlugin rp = plugins.get(e.getClass());
            rp.draw(g, e);
        }
        e.child.forEach(c -> drawEntity(g, c));
    }

    /**
     * draw debug grid on viewport.
     *
     * @param g    Graphics API
     * @param step Step to draw for grid
     */
    private void drawDisplayDebugInfo(Game game, Graphics2D g, int step) {

        PhysicEngine pe = game.getPhysicEngine();

        g.setFont(g.getFont().deriveFont(9.0f));

        preDraw(g, false);

        postDraw(g, false);

        g.setColor(Color.ORANGE);
        game.getEntities().forEach((name, e) -> {
            GameEntity entity = (GameEntity) e;
            preDraw(g, entity.isStickToCamera());

            drawEntityDebugBox(g, entity, Color.ORANGE);

            if (game.getDebug() > 2) {
                drawEntityDebugLine(g, entity);
            }
            postDraw(g, entity.isStickToCamera());

        });
        g.drawRect(0, 0,
                pe.getWorld().getPlayArea().width,
                pe.getWorld().getPlayArea().height);
    }

    private void drawPlayAreaGrid(Graphics2D g, int step, PhysicEngine pe) {
        preDraw(g, false);
        g.setColor(Color.GRAY);
        for (int x = 0; x < pe.getWorld().getPlayArea().getWidth(); x += step) {
            g.drawLine(x, 0, x, (int) pe.getWorld().getPlayArea().getHeight());
        }
        for (int y = 0; y < pe.getWorld().getPlayArea().getHeight(); y += step) {
            g.drawLine(0, y, (int) pe.getWorld().getPlayArea().getWidth(), y);
        }
        g.setColor(Color.CYAN);
        g.drawRect(0, 0,
                (int) pe.getWorld().getPlayArea().getWidth(),
                (int) pe.getWorld().getPlayArea().getHeight());
        postDraw(g, false);
    }

    private void drawEntityDebugBox(Graphics2D g, GameEntity e, Color c) {
        g.setColor(c);
        g.draw(e.box);
        g.setColor(Color.MAGENTA);
        if ((double) e.getAttribute("attraction.distance", 0.0) > 0.0) {
            double d = (double) e.getAttribute("attraction.distance", 0.0);
            float[] dash1 = { 2f, 0f, 2f };
            BasicStroke bs1 = new BasicStroke(1,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND,
                    1.0f,
                    dash1,
                    2f);
            g.setStroke(bs1);
            g.draw(new Ellipse2D.Double(
                    e.position.x - (d - e.size.x) * 0.5,
                    e.position.y - (d - e.size.y) * 0.5,
                    d, d));
            g.setStroke(new BasicStroke());
        }
    }

    private void drawEntityDebugLine(Graphics2D g, GameEntity entity) {
        if (debugEntityNames.contains(entity.name)) {
            g.setColor(Color.WHITE);
            int hl = g.getFontMetrics().getHeight();
            int il = 0;
            for (String s : entity.getDebugInfo()) {
                g.drawString(s, (int) (entity.position.x + entity.size.x + 4.0), (int) entity.position.y + il);
                il += hl - 3;
            }
        }
    }

    private void drawCameraDebug(Graphics2D g, CameraEntity camera) {
        g.setColor(Color.YELLOW);
        g.drawRect(10, 10, camera.viewport.width - 20, camera.viewport.height - 20);
        g.setColor(Color.WHITE);
        g.drawString(String.format("cam: %s", camera.name), 20, 20);
        g.drawString(String.format("pos: %04.2f,%04.2f", camera.position.x, camera.position.y), 20, 32);
        g.drawString(String.format("rot: %04.2f°", Math.toDegrees(camera.rotation)), 20, 44);
        g.drawString(String.format("target: %s", camera.name), 20, 56);
    }
}
