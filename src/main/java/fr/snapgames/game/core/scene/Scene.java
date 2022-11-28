package fr.snapgames.game.core.scene;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.behavior.Behavior;
import fr.snapgames.game.core.entity.CameraEntity;
import fr.snapgames.game.core.entity.Entity;

import java.awt.*;
import java.util.Collection;
import java.util.Map;

public interface Scene {

    String getName();

    Map<String, Entity> getEntities();

    Collection<Behavior<Scene>> getBehaviors();

    void initialize(Game g);

    void load(Game g);

    Scene setCamera(String camName);

    CameraEntity getCamera();

    Scene addCamera(CameraEntity cam);

    CameraEntity getCamera(String name);

    void create(Game g);

    void input(Game g);

    void update(Game g, double elapsed);

    void draw(Game g, Graphics2D g2D);

    void dispose(Game g);
}
