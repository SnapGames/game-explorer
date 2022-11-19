package fr.snapgames.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JPanel;

import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.entity.EntityType;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.TextEntity;
import fr.snapgames.game.core.entity.behaviors.Behavior;
import fr.snapgames.game.core.entity.behaviors.CameraInputBehavior;
import fr.snapgames.game.core.entity.behaviors.EnemyFollowerBehavior;
import fr.snapgames.game.core.entity.behaviors.PlayerInputBehavior;
import fr.snapgames.game.core.io.Input;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.physic.Material;
import fr.snapgames.game.core.math.physic.World;
import fr.snapgames.game.core.utils.I18n;

/**
 * Main Game Java2D test.
 *
 * @author Frédéric Delorme
 * @since 2022
 */
public class Game extends JPanel {

    // Frames to be rendered
    double FPS = 60.0;
    double fpsDelay = 1000000.0 / 60.0;
    double scale = 2.0;
    // some internal flags
    boolean debug = true;
    boolean exit = false;
    boolean pause = false;

    /**
     * the Test mode is a flag to deactivate the while Loop in the {@link Game#loop}
     * method.
     */
    private boolean testMode;

    // Internal components
    BufferedImage buffer;
    Color clearColor = Color.BLACK;
    Configuration config;
    I18n i18n;
    JFrame frame;
    Input input;
    // Internal GameEntity cache
    Map<String, GameEntity> entities = new HashMap<>();
    World world;
    Camera currentCamera = null;

    public Game() {
        this("/game.properties", false);
    }

    public Game(String configFilePath, boolean testMode) {

        this.testMode = testMode;
        config = new Configuration(configFilePath);

        debug = config.getBoolean("game.debug", false);

        FPS = config.getDouble("game.screen.fps", 60.0);
        fpsDelay = 1000000.0 / FPS;

        input = new Input(this);
        String title = I18n.get("game.title");
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        double scale = config.getDouble("game.screen.scale", 2.0);
        int width = (int) (scale * config.getInteger("game.screen.width", 320));
        int height = (int) (scale * config.getInteger("game.screen.height", 200));
        Dimension dim = new Dimension(width, height);

        // define Window content and size.
        frame.setLayout(new GridLayout());

        frame.setContentPane(this);

        frame.setSize(dim);
        frame.setPreferredSize(dim);
        frame.setMinimumSize(dim);
        frame.setMaximumSize(dim);

        setBackground(Color.BLACK);
        frame.setIgnoreRepaint(true);
        frame.enableInputMethods(true);
        frame.setFocusTraversalKeysEnabled(false);
        frame.setLocationByPlatform(false);
        // define Window content and size.
        frame.setLayout(new GridLayout());
        getLayout().layoutContainer(frame);

        frame.setContentPane(this);
        frame.getContentPane().setPreferredSize(dim);

        frame.addKeyListener(input);
        frame.pack();

        frame.setVisible(true);
        if (frame.getBufferStrategy() == null) {
            frame.createBufferStrategy(config.getInteger("game.buffer.strategy", 2));
        }
        buffer = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Initialize game.
     *
     * @param args Java command line arguments
     */
    private void initialize(String[] args) {
        config.parseArguments(args);
        create((Graphics2D) frame.getGraphics());
    }

    private void create(Graphics2D g) {

        int worldWidth = config.getInteger("game.world.width", 1000);
        int worldHeight = config.getInteger("game.world.height", 1000);

        world = new World(new Dimension(worldWidth, worldHeight),
                new Vector2D(0, -0.981));

        TextEntity score = (TextEntity) new TextEntity("score")
                .setText("Score")
                .setFont(g.getFont().deriveFont(20.0f))
                .setPosition(new Vector2D(worldWidth - 80, 25))
                .setSize(new Vector2D(16, 16))
                .setColor(Color.WHITE)
                .stickToCamera(true);
        add(score);

        GameEntity player = new GameEntity("player")
                .setPosition(new Vector2D(worldWidth / 2.0, worldHeight / 2.0))
                .setSize(new Vector2D(16, 16))
                .setColor(Color.BLUE)
                .setMaterial(new Material("playerMat", 1.0, 0.21, 1.0))
                .setAttribute("maxSpeed", 6.0)
                .setAttribute("maxAcceleration", 2.0)
                .setAttribute("mass", 8.0)
                .addBehavior(new PlayerInputBehavior());
        add(player);

        for (int i = 0; i < 10; i++) {
            GameEntity e = new GameEntity("en_" + i)
                    .setPosition(new Vector2D(Math.random() * worldWidth, Math.random() * worldHeight))
                    .setSize(new Vector2D(12, 12))
                    .setColor(Color.RED)
                    .setType(EntityType.CIRCLE)
                    .setMaterial(new Material("enemyMat", 1.0, 0.1, 1.0))
                    .setAttribute("maxSpeed", 8.0)
                    .setAttribute("maxAcceleration", 2.5)
                    .setAttribute("mass", 5.0)
                    .setAttribute("attractionDistance", 80.0)
                    .setAttribute("attractionForce", 2.0)
                    .addBehavior(new EnemyFollowerBehavior());

            add(e);
        }

        int vpWidth = config.getInteger("game.viewport.width", 320);
        int vpHeight = config.getInteger("game.viewport.height", 200);

        Camera cam = (Camera) new Camera("camera")
                .setTarget(player)
                .setTween(0.1)
                .setViewport(new Dimension(vpWidth, vpHeight))
                .setRotation(0.0)
                .addBehavior(new CameraInputBehavior());
        setCurrentCamera(cam);
    }

    public void add(GameEntity e) {
        entities.put(e.name, e);
    }

    /**
     * Draw all things on screen.
     *
     * @param realFPS displayed Frame Per Seconds.
     */
    private void draw(long realFPS) {
        double scale = config.getDouble("game.screen.scale", 2.0);
        if (Optional.ofNullable(buffer).isPresent()) {
            Graphics2D g = buffer.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            // clear scene
            g.setColor(clearColor);
            g.clearRect(0, 0, this.getWidth(), this.getHeight());
            // draw Scene
            for (GameEntity entity : entities.values()) {
                if (Optional.ofNullable(currentCamera).isPresent() && !entity.isStickToCamera()) {
                    currentCamera.preDraw(g);
                }
                for (Behavior b : entity.behaviors) {
                    b.draw(this, entity, g);
                }
                entity.draw(g);
                if (Optional.ofNullable(currentCamera).isPresent() && !entity.isStickToCamera()) {
                    currentCamera.postDraw(g);
                }
            }
            if (debug) {
                drawDisplayDebugInfo(g, 32);
            }
            if (Optional.ofNullable(currentCamera).isPresent() && debug) {
                drawCameraDebug(g, currentCamera);
            }
            if (pause) {
                g.setColor(new Color(0.3f, 0.6f, 0.4f, 0.9f));
                g.fillRect(0, (currentCamera.viewport.height - 24) / 2, currentCamera.viewport.width, 24);
                g.setColor(Color.WHITE);
                g.setFont(g.getFont().deriveFont(14.0f).deriveFont(Font.BOLD));
                String pauseTxt = I18n.get("game.state.pause.message");
                int lng = g.getFontMetrics().stringWidth(pauseTxt);
                g.drawString(pauseTxt, (currentCamera.viewport.width - lng) / 2,
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
                    if (debug) {
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

    /**
     * draw debug grid on viewport.
     *
     * @param g    Graphics API
     * @param step Step to draw for grid
     */
    private void drawDisplayDebugInfo(Graphics2D g, int step) {

        g.setFont(g.getFont().deriveFont(8.0f));

        if (Optional.ofNullable(currentCamera).isPresent()) {
            currentCamera.preDraw(g);
        }
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < world.getPlayArea().getWidth(); x += step) {
            g.drawLine(x, 0, x, (int) world.getPlayArea().getHeight());
        }
        for (int y = 0; y < world.getPlayArea().getHeight(); y += step) {
            g.drawLine(0, y, (int) world.getPlayArea().getWidth(), y);
        }
        g.setColor(Color.CYAN);
        g.drawRect(0, 0, (int) world.getPlayArea().getWidth(), (int) world.getPlayArea().getHeight());
        if (Optional.ofNullable(currentCamera).isPresent()) {
            currentCamera.postDraw(g);
        }

        g.setColor(Color.ORANGE);
        entities.forEach((k, v) -> {
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
        g.drawRect(0, 0, world.getPlayArea().width, world.getPlayArea().height);
    }

    private void drawCameraDebug(Graphics2D g, Camera camera) {
        g.drawRect(10, 10, camera.viewport.width - 20, camera.viewport.height - 20);
        g.drawString(String.format("cam: %s", camera.name), 20, 20);
        g.drawString(String.format("pos: %04.2f,%04.2f", camera.position.x, camera.position.y), 20, 32);
        g.drawString(String.format("rot: %04.2f", Math.toDegrees(camera.rotation)), 20, 44);
        g.drawString(String.format("targ: %s", camera.target.name), 20, 56);
    }

    /**
     * update game entities according to input
     */
    private void input() {
        for (GameEntity e : entities.values()) {
            for (Behavior b : e.behaviors) {
                b.input(this, e);
            }
        }
        for (Behavior b : currentCamera.behaviors) {
            b.input(this, currentCamera);
        }
    }

    /**
     * Update entities.
     *
     * @param elapsed elapsed time since previous call.
     */
    public void update(double elapsed) {
        for (GameEntity entity : entities.values()) {
            entity.forces.add(world.getGravity().negate());
            entity.update(this, elapsed);
            constrainEntityToWorld(world, entity);
        }
        for (Behavior b : currentCamera.behaviors) {
            b.update(this, currentCamera, elapsed);
        }
        currentCamera.update(elapsed);
    }

    /**
     * Constrain the GameEntity ge to stay in the world play area.
     *
     * @param world the defne World for the Game
     * @param ge    the GameEntity to be checked against world's play area
     *              constrains
     * @see GameEntity
     * @see World
     */
    private void constrainEntityToWorld(World world, GameEntity ge) {
        if (world.isNotContaining(ge)) {
            if (ge.position.x + ge.size.x > world.getPlayArea().width) {
                ge.position.x = world.getPlayArea().width - ge.size.x;
            }
            if (ge.position.x < 0) {
                ge.position.x = 0;
            }
            if (ge.position.y + ge.size.y > world.getPlayArea().height) {
                ge.position.y = world.getPlayArea().height - ge.size.y;
            }
            if (ge.position.y < 0) {
                ge.position.y = 0;
            }
            ge.speed = ge.speed.multiply(-ge.material.elasticity);
        }
    }

    /**
     * Request to close this Window frame.
     */
    public void close() {
        dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        frame.dispose();
    }

    /**
     * Main Game loop
     */
    public void loop() {
        // elapsed Game Time
        double start = 0;
        double end = 0;
        double dt = 0;
        // FPS measure
        long frames = 0;
        long realFPS = 0;
        long timeFrame = 0;
        while (!exit && !testMode) {
            start = System.nanoTime() / 1000000.0;
            input();
            if (!pause) {
                update(dt * .04);
            }

            frames += 1;
            timeFrame += dt;
            if (timeFrame > 1000) {
                realFPS = frames;
                frames = 0;
                timeFrame = 0;
            }

            draw(realFPS);
            waitUntilStepEnd(dt);

            end = System.nanoTime() / 1000000.0;
            dt = end - start;
        }

    }

    private void waitUntilStepEnd(double dt) {
        if (dt < fpsDelay) {
            try {
                Thread.sleep((long) (fpsDelay - dt) / 1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Main run method.
     *
     * @param args Command line arguments
     */
    public void run(String[] args) {
        initialize(args);
        loop();
        close();
    }

    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'p') {
            this.pause = !this.pause;
        }
    }

    public Map<String, GameEntity> getEntities() {
        return entities;
    }

    public void setCurrentCamera(Camera cam) {
        this.currentCamera = cam;
    }

    public Camera getCurrentCamera() {
        return currentCamera;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Input getInput() {
        return input;
    }

    public void requestExit(boolean e) {
        this.exit = e;
    }

    /**
     * Entry point for executing game.
     *
     * @param args list of command line arguments
     */
    public static void main(String[] args) {

        Game game = new Game();
        game.run(args);
    }
}
