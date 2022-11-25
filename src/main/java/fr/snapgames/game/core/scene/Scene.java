package fr.snapgames.game.core.scene;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.CameraEntity;

public interface Scene {

    String getName();

    void initialize(Game g);

    void load(Game g);

    Scene setCamera(String camName);

    CameraEntity getCamera();

    Scene addCamera(CameraEntity cam);

    CameraEntity getCamera(String name);

    void create(Game g);

    void input(Game g);

    void update(Game g, double elapsed);

    void draw(Game g);

    void dispose(Game g);

}
