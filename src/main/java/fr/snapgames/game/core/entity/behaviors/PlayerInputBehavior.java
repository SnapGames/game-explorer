package fr.snapgames.game.core.entity.behaviors;

import java.awt.Graphics2D;

import java.awt.event.KeyEvent;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.io.Input;
import fr.snapgames.game.core.math.Vector2D;

public class PlayerInputBehavior implements Behavior {

    @Override
    public void update(Game game, GameEntity entity, double dt) {

    }

    @Override
    public void input(Game game, GameEntity entity) {
        Input input = game.getInput();
        double accel = (Double) entity.getAttribute("speedStep", 1.0);
        if (input.getKey(KeyEvent.VK_ESCAPE)) {
            game.requestExit(true);
            ;
        }

        if (input.getKey(KeyEvent.VK_UP)) {
            entity.forces.add(new Vector2D(0, -accel));
        }
        if (input.getKey(KeyEvent.VK_DOWN)) {
            entity.forces.add(new Vector2D(0, accel));
        }
        if (input.getKey(KeyEvent.VK_RIGHT)) {
            entity.forces.add(new Vector2D(accel, 0));
        }
        if (input.getKey(KeyEvent.VK_LEFT)) {
            entity.forces.add(new Vector2D(-accel, 0));
        }
    }

    @Override
    public void draw(Game game, GameEntity entity, Graphics2D g) {

    }

}
