package fr.snapgames.game.core.entity.behaviors;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.io.Input;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CameraInputBehavior implements Behavior {
    @Override
    public void update(Game game, GameEntity entity, double dt) {

    }

    @Override
    public void input(Game game, GameEntity entity) {
        Camera cam = (Camera) entity;
        Input input = game.getInput();
        if (input.getKey(KeyEvent.VK_1)) {
            cam.rotation += 0.01;
        }
        if (input.getKey(KeyEvent.VK_2)) {
            cam.rotation -= 0.01;
        }
        if (input.getKey(KeyEvent.VK_0)) {
            cam.rotation = 0.00;
        }
    }

    @Override
    public void draw(Game game, GameEntity entity, Graphics2D g) {

    }
}
