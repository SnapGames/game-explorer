package fr.snapgames.game.core.scene;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.behavior.Behavior;
import fr.snapgames.game.core.entity.CameraEntity;
import fr.snapgames.game.core.entity.Entity;
import fr.snapgames.game.core.entity.GameEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractScene implements Scene {

    protected Game game;

    /**
     * Map of declared cameras on the scene.
     */
    protected Map<String, CameraEntity> cameras = new ConcurrentHashMap<>();
    private Collection<Behavior<Scene>> behaviors = new ArrayList<>();

    public AbstractScene(Game g) {
        this.game = g;
    }

    @Override
    public Scene setCamera(String camName) {
        game.setCurrentCamera(cameras.get(camName));
        return this;
    }

    @Override
    public CameraEntity getCamera() {
        return game.getCurrentCamera();
    }

    @Override
    public Scene addCamera(CameraEntity cam) {
        this.cameras.put(cam.name, cam);
        if (Optional.ofNullable(game.getCurrentCamera()).isEmpty()) {
            game.setCurrentCamera(cam);
        }
        return this;
    }

    @Override
    public CameraEntity getCamera(String name) {
        return this.cameras.get(name);
    }

    public void create(Game g) {
        this.game = g;
    }

    @Override
    public void load(Game g) {
    }


    @Override
    public void input(Game g) {
        behaviors.forEach(b -> b.input(game, this));
    }

    @Override
    public void update(Game g, double elapsed) {
        behaviors.forEach(b -> b.update(game, this, elapsed));
    }

    @Override
    public void draw(Game g, Graphics2D g2D) {
        behaviors.forEach(b -> b.draw(game, this, g2D));
    }

    /**
     * Add a {@link GameEntity} to the Scene
     *
     * @param e the {@link GameEntity} to be added.
     */
    protected void add(GameEntity e) {
        game.add(e);
    }

    /**
     * Retrieve a {@link GameEntity} on its name in the {@link Scene}.
     *
     * @param gameObjectName the name of the {@link GameEntity} to be retrieved into
     *                       the {@link Scene}.
     * @return the corresponding {@link GameEntity} instance.
     */
    protected GameEntity get(String gameObjectName) {
        return (GameEntity) game.getEntities().get(gameObjectName);
    }


    @Override
    public Collection<Behavior<Scene>> getBehaviors() {
        return behaviors;
    }

    public Map<String, Entity> getEntities() {
        return this.game.getEntities();
    }
}
