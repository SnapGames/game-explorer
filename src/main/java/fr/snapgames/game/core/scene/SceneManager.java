package fr.snapgames.game.core.scene;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.config.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SceneManager {

    private Game game;
    /**
     * Current active Scene
     */
    private Scene current;
    /**
     * list of available scene loaded from configuration.
     */
    private final Map<String, Scene> scenes = new ConcurrentHashMap<>();

    /**
     * Create the SceneManager upon the {@link Game} component
     *
     * @param g the parent Game component {@link Game}
     */
    public SceneManager(Game g) {
        this.game = g;
        initialize(g.getConfiguration());
    }

    private void initialize(Configuration config) {
        if (!config.getString("game.scene.list", "").equals("") && !config.getString("game.scene.default", "").equals("")) {
            String[] scenesList = config.getString("game.scene.list", "").split(",");
            Arrays.asList(scenesList).forEach(s -> {
                String[] kv = s.split(":");
                try {
                    Class<? extends Scene> sceneToAdd = (Class<? extends Scene>) Class.forName(kv[1]);
                    add(sceneToAdd);
                    System.out.printf("INFO : SceneManager | Add scene %s:%s%n", kv[0], kv[1]);
                } catch (ClassNotFoundException e) {
                    System.out.printf("ERR : SceneManager | Unable to load class %s%n", kv[1]);
                }
            });
            activateScene(config.getString("game.scene.default", ""));
        } else {
            System.err.println("ERR : SceneManager | No scene defined into the configuration");
        }

    }

    private void activateScene(String sceneName) {
        this.current = scenes.get(sceneName);
    }

    public void add(Class<? extends Scene> sceneClass) {
        Scene scn;
        try {
            scn = sceneClass.getConstructor(Game.class).newInstance(game);
            scenes.put(scn.getName(), scn);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException e) {
            System.out.printf("ERR: unable to add scene %s%n", sceneClass.getName());
        }
    }

    public Scene getCurrent() {
        return current;
    }

    /**
     * Return the list of scenes loaded from configuration into SceneManager.
     *
     * @return the current instantiated Scene list
     */
    public Collection<Scene> getSceneList() {
        return this.scenes.values();
    }

    /**
     * Return a specific scene by its name from tha scenes list.
     *
     * @param sceneName the scene name to be retrieved from the list.
     * @return the Scene instance corresponding to the sceneName.
     */
    public Scene getScene(String sceneName) {
        return scenes.get(sceneName);
    }
}
