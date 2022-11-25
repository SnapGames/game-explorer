package fr.snapgames.game;

import fr.snapgames.game.core.behavior.Behavior;
import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.*;
import fr.snapgames.game.core.entity.behaviors.*;
import fr.snapgames.game.core.gfx.Renderer;
import fr.snapgames.game.core.io.Input;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.physic.Influencer;
import fr.snapgames.game.core.math.physic.Material;
import fr.snapgames.game.core.math.physic.PhysicEngine;
import fr.snapgames.game.core.scene.SceneManager;
import fr.snapgames.game.core.service.EntityManager;
import fr.snapgames.game.core.utils.I18n;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Main Game Java2D test.
 *
 * @author Frédéric Delorme
 * @since 2022
 */
public class Game extends JPanel {

    // Frames to be rendered
    private double FPS = 60.0;
    private double fpsDelay = 1000000.0 / 60.0;
    private double scale = 2.0;
    // some internal flags
    private int debug = 0;
    private boolean exit = false;
    private boolean pause = false;

    private long realFPS = 0;

    /**
     * the Test mode is a flag to deactivate the while Loop in the {@link Game#loop}
     * method.
     */
    private boolean testMode;

    private Configuration config;
    private I18n i18n;
    private EntityManager entityMgr;
    private PhysicEngine pe;
    private Renderer renderer;
    private Input input;

    private SceneManager sceneMgr;
    private JFrame frame;

    // Internal GameEntity cache
    private Map<String, Entity> entities = new HashMap<>();
    private CameraEntity currentCamera = null;
    private double startTime;

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

        entityMgr = new EntityManager(this);
        pe = new PhysicEngine(this);
        renderer = new Renderer(this);
        sceneMgr = new SceneManager(this);
        startTime = System.currentTimeMillis();

    }

    private JFrame createWindow(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // define Window content and size.
        frame.setLayout(new GridLayout());
        frame.setContentPane(this);

        Dimension dim = new Dimension(width, height + frame.getInsets().top);
        this.setSize(dim);
        this.setPreferredSize(dim);
        this.setMinimumSize(dim);
        this.setMaximumSize(dim);
        frame.setIconImage(Toolkit.getDefaultToolkit()
                .getImage(getClass()
                        .getResource("/images/sg-logo-image.png")));

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
        sceneMgr.getCurrent().initialize(this);
        sceneMgr.getCurrent().load(this);
        create((Graphics2D) frame.getGraphics());
    }

    private void create(Graphics2D g) {
        sceneMgr.getCurrent().create(this);
    }

    public void add(GameEntity e) {
        if (e instanceof Influencer) {
            getPhysicEngine().getWorld().add((Influencer) e);
        }
        entityMgr.add(e);
    }

    /**
     * update game entities according to input
     */
    private void input() {
        for (Entity e : getEntities().values()) {
            for (Behavior b : e.behaviors) {
                b.input(this, e);
            }
        }
        if (Optional.ofNullable(currentCamera).isPresent()) {
            for (Behavior b : currentCamera.behaviors) {
                b.input(this, currentCamera);
            }
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
        double gameTime = currentTime - startTime;
        double previousTime = currentTime;
        double dt = 0;
        // FPS measure
        long frames = 0;
        double timeFrame = 0.0;
        while (!exit && !testMode) {
            currentTime = System.currentTimeMillis();
            // delta-time in sec.
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
                System.err.println("unable to Wait some millis:" + e.getMessage());
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
        if (e.getKeyChar() == 'p' ||
                e.getKeyCode() == KeyEvent.VK_PAUSE) {
            this.pause = !this.pause;
        }
        if (e.getKeyChar() == 'q') {
            this.exit = true;
        }
        if (e.getKeyChar() == 'd') {
            debug = (debug + 1 < 6 ? debug + 1 : 0);
        }
        if (e.getKeyChar() == 'g') {
            double gy = getPhysicEngine().getWorld().getGravity().y;
            getPhysicEngine().getWorld().getGravity().y = -gy;
        }
    }

    public Map<String, Entity> getEntities() {
        return entityMgr.getEntities();
    }

    public void setCurrentCamera(CameraEntity cam) {
        this.currentCamera = cam;
    }

    public CameraEntity getCurrentCamera() {
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

    public long getCurrentGameTime() {
        return System.currentTimeMillis() - (long) startTime;
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
