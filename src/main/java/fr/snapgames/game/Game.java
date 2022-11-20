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

import javax.swing.*;

import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.entity.EntityType;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.TextEntity;
import fr.snapgames.game.core.entity.behaviors.*;
import fr.snapgames.game.core.gfx.Renderer;
import fr.snapgames.game.core.io.Input;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.physic.Material;
import fr.snapgames.game.core.math.physic.PhysicEngine;
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
    int debug = 0;
    boolean exit = false;
    boolean pause = false;

    long realFPS = 0;

    /**
     * the Test mode is a flag to deactivate the while Loop in the {@link Game#loop}
     * method.
     */
    private boolean testMode;

    Configuration config;
    I18n i18n;
    PhysicEngine pe;
    Renderer renderer;
    Input input;
    JFrame frame;


    // Internal GameEntity cache
    Map<String, GameEntity> entities = new HashMap<>();
    Camera currentCamera = null;

    public Game() {

        this("/game.properties", false);
    }

    public Game(String configFilePath, boolean testMode) {

        this.testMode = testMode;
        config = new Configuration(configFilePath);
        debug = config.getInteger("game.debug", 0);
        FPS = config.getDouble("game.screen.fps", 60.0);
        fpsDelay = 1000000.0 / FPS;

        double scale = config.getDouble("game.screen.scale", 2.0);
        int width = (int) (scale * config.getInteger("game.screen.width", 320));
        int height = (int) (scale * config.getInteger("game.screen.height", 200));
        String title = I18n.get("game.title");

        input = new Input(this);
        frame = createWindow(title, width, height);

        pe = new PhysicEngine(this);
        renderer = new Renderer(this);


    }

    private JFrame createWindow(String title, int width, int height) {
        Dimension dim = new Dimension(width, height);
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        return frame;
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
        Vector2D gravity = config.getVector2D("game.world.gravity", new Vector2D(0, -0.981));
        int screenWidth = config.getInteger("game.screen.width", 320);

        pe.setWorld(
                new World(new Dimension(worldWidth, worldHeight),
                        gravity));

        GameEntity player = new GameEntity("player")
                .setPosition(new Vector2D(worldWidth / 2.0, worldHeight / 2.0))
                .setSize(new Vector2D(16, 16))
                .setColor(Color.BLUE)
                .setMaterial(new Material("playerMat", 1.0, 0.21, 1.0))
                .setAttribute("maxSpeed", 800.0)
                .setAttribute("maxAcceleration", 800.0)
                .setAttribute("mass", 80.0)
                .setAttribute("speedStep", 300.0)
                .setAttribute("score", 0)
                .addBehavior(new PlayerInputBehavior());
        add(player);
        TextEntity score = (TextEntity) new TextEntity("score")
                .setText("%05d")
                .setValue(player, "score", 0)
                .setFont(g.getFont().deriveFont(20.0f))
                .setPosition(new Vector2D(screenWidth * 0.8, 25))
                .setSize(new Vector2D(16, 16))
                .setColor(Color.WHITE)
                .stickToCamera(true);
        add(score);

        for (int i = 0; i < 10; i++) {
            GameEntity e = new GameEntity("en_" + i)
                    .setPosition(new Vector2D(Math.random() * worldWidth, Math.random() * worldHeight))
                    .setSize(new Vector2D(12, 12))
                    .setColor(Color.RED)
                    .setType(EntityType.CIRCLE)
                    .setMaterial(new Material("enemyMat", 1.1, 0.70, 0.98))
                    .setAttribute("maxSpeed", 800.0)
                    .setAttribute("maxAcceleration", 800.0)
                    .setAttribute("mass", 30.0)
                    .setAttribute("attractionDistance", 80.0)
                    .setAttribute("attractionForce", 40.0)
                    .addBehavior(new EnemyFollowerBehavior());

            add(e);
        }

        int vpWidth = config.getInteger("game.screen.width", 320);
        int vpHeight = config.getInteger("game.screen.height", 200);

        Camera cam = (Camera) new Camera("camera")
                .setTarget(player)
                .setTween(0.1)
                .setViewport(new Dimension(vpWidth, vpHeight))
                .setRotation(0.0)
                .addBehavior(new CameraInputBehavior())
                .addBehavior(new CameraUpdateBehavior());

        setCurrentCamera(cam);
    }

    public void add(GameEntity e) {
        entities.put(e.name, e);
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
        pe.update(this, elapsed);
    }

    /**
     * Draw all things on screen.
     *
     * @param realFPS displayed Frame Per Seconds.
     */
    private void draw(long realFPS) {
        renderer.draw(this, realFPS);
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
        double currentTime = System.currentTimeMillis();
        double previousTime = currentTime;
        double dt = 0;
        // FPS measure
        long frames = 0;
        double timeFrame = 0.0;
        while (!exit && !testMode) {
            currentTime = System.currentTimeMillis();
            // delta-time  in sec.
            dt = (currentTime - previousTime) * 0.001;
            input();
            if (!pause) {
                update(dt);
            }

            frames += 1;
            timeFrame += dt;
            if (timeFrame > 1.0) {
                realFPS = frames;
                frames = 0;
                timeFrame = 0;
            }

            draw(realFPS);
            waitUntilStepEnd(dt);

            previousTime = currentTime;
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
        if (e.getKeyCode() == KeyEvent.VK_P ||
                e.getKeyCode() == KeyEvent.VK_PAUSE) {
            this.pause = !this.pause;
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.exit = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            debug = (debug + 1 < 6 ? debug + 1 : 0);
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

    public Input getInput() {
        return input;
    }

    public void requestExit(boolean e) {
        this.exit = e;
    }


    public JFrame getFrame() {
        return frame;
    }

    public Configuration getConfiguration() {
        return config;
    }

    public int getDebug() {
        return debug;
    }

    public PhysicEngine getPhysicEngine() {
        return this.pe;
    }

    public boolean getPause() {
        return pause;
    }

    public long getRealFPS() {
        return realFPS;
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
