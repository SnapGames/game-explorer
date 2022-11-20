package fr.snapgames.game.core.gfx;

import com.sun.source.util.Plugin;
import fr.snapgames.game.Game;
import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.behaviors.Behavior;
import fr.snapgames.game.core.entity.behaviors.Entity;
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
    Configuration config;
    JFrame frame;
    BufferedImage buffer;
    Camera currentCamera;
    // Internal components
    Color clearColor = Color.BLACK;

    private Map<Class<? extends Entity>, RendererPlugin> plugins = new HashMap<>();

    public Renderer(Game g) {
        config = g.getConfiguration();
        frame = g.getFrame();
        buffer = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
        addPlugin(new GameEntityDrawPlugin());
        addPlugin(new TextEntityDrawPlugin());
    }

    private void addPlugin(RendererPlugin rp) {
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
            for (GameEntity entity : game.getEntities().values()) {
                if (Optional.ofNullable(currentCamera).isPresent() && !entity.isStickToCamera()) {
                    currentCamera.preDraw(g);
                }
                for (Behavior b : entity.behaviors) {
                    b.draw(game, entity, g);
                }
                drawEntity(g, entity);
                if (Optional.ofNullable(currentCamera).isPresent() && !entity.isStickToCamera()) {
                    currentCamera.postDraw(g);
                }
            }
            if (game.getDebug() > 0) {
                drawDisplayDebugInfo(game, g, 32);
            }
            if (Optional.ofNullable(currentCamera).isPresent()
                    && game.getDebug() > 0) {
                drawCameraDebug(g, currentCamera);
            }
            if (game.getPause()) {

                g.setColor(new Color(0.3f, 0.6f, 0.4f, 0.9f));
                g.fillRect(0, (currentCamera.viewport.height - 24) / 2, currentCamera.viewport.width, 24);

                g.setColor(Color.WHITE);
                g.setFont(g.getFont().deriveFont(14.0f).deriveFont(Font.BOLD));

                String pauseTxt = I18n.get("game.state.pause.message");
                int lng = g.getFontMetrics().stringWidth(pauseTxt);

                g.drawString(
                        pauseTxt,
                        (currentCamera.viewport.width - lng) / 2,
                        (currentCamera.viewport.height + 12) / 2);
            }
            g.dispose();
            // draw image to screen.
            if (Optional.ofNullable(frame).isPresent()) {
                if (frame.getBufferStrategy() != null) {
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
        }
    }

    public void drawEntity(Graphics2D g, Entity e) {
        if (plugins.containsKey(e.getClass())) {
            RendererPlugin rp = plugins.get(e.getClass());
            rp.draw(g, e);
        }
    }

    /**
     * draw debug grid on viewport.
     *
     * @param g    Graphics API
     * @param step Step to draw for grid
     */
    private void drawDisplayDebugInfo(Game game, Graphics2D g, int step) {

        PhysicEngine pe = game.getPhysicEngine();

        g.setFont(g.getFont().deriveFont(8.0f));

        if (Optional.ofNullable(currentCamera).isPresent()) {
            currentCamera.preDraw(g);
        }
        g.setColor(Color.LIGHT_GRAY);
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
        if (Optional.ofNullable(currentCamera).isPresent()) {
            currentCamera.postDraw(g);
        }

        g.setColor(Color.ORANGE);
        game.getEntities().forEach((k, v) -> {
            if (Optional.ofNullable(currentCamera).isPresent() && !v.isStickToCamera()) {
                currentCamera.preDraw(g);
            }

            g.drawRect((int) v.position.x, (int) v.position.y,
                    (int) v.size.x, (int) v.size.y);
            int il = 0;
            for (String s : v.getDebugInfo()) {
                g.drawString(s, (int) (v.position.x + v.size.x + 4.0), (int) v.position.y + il);
                il += 10;
            }
            if (Optional.ofNullable(currentCamera).isPresent() && !v.isStickToCamera()) {
                currentCamera.postDraw(g);
            }

        });
        g.drawRect(0, 0,
                pe.getWorld().getPlayArea().width,
                pe.getWorld().getPlayArea().height);
    }

    private void drawCameraDebug(Graphics2D g, Camera camera) {
        g.drawRect(10, 10, camera.viewport.width - 20, camera.viewport.height - 20);
        g.drawString(String.format("cam: %s", camera.name), 20, 20);
        g.drawString(String.format("pos: %04.2f,%04.2f", camera.position.x, camera.position.y), 20, 32);
        g.drawString(String.format("rot: %04.2f", Math.toDegrees(camera.rotation)), 20, 44);
        g.drawString(String.format("targ: %s", camera.target.name), 20, 56);
    }
}
