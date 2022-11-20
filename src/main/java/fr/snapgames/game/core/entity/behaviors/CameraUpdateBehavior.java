package fr.snapgames.game.core.entity.behaviors;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.entity.GameEntity;

import java.awt.*;

public class CameraUpdateBehavior implements Behavior {

    @Override
    public void update(Game game, GameEntity entity, double dt) {
        Camera c = (Camera) entity;
        c.position.x += Math
                .ceil((c.target.position.x + (c.target.size.x * 0.5) - ((c.viewport.width) * 0.5) - c.position.x)
                        * c.tween * dt);
        c.position.y += Math
                .ceil((c.target.position.y + (c.target.size.y * 0.5) - ((c.viewport.height) * 0.5) - c.position.y)
                        * c.tween * dt);
    }

    @Override
    public void input(Game game, GameEntity entity) {

    }

    @Override
    public void draw(Game game, GameEntity entity, Graphics2D g) {

    }
}
