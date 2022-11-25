package fr.snapgames.game.core.entity.behaviors;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.behavior.Behavior;
import fr.snapgames.game.core.entity.CameraEntity;
import fr.snapgames.game.core.entity.Entity;
import fr.snapgames.game.core.entity.GameEntity;

import java.awt.*;

public class CameraUpdateBehavior implements Behavior<Entity> {

    @Override
    public void update(Game game, Entity entity, double dt) {
        CameraEntity c = (CameraEntity) entity;
        GameEntity target = (GameEntity) game.getEntities().get(c.target);
        dt *= 100;
        c.position.x += Math
                .ceil((target.position.x + (target.size.x * 0.5) - ((c.viewport.width) * 0.5) - c.position.x)
                        * c.tween * dt);
        c.position.y += Math
                .ceil((target.position.y + (target.size.y * 0.5) - ((c.viewport.height) * 0.5) - c.position.y)
                        * c.tween * dt);
    }

    @Override
    public void input(Game game, Entity entity) {

    }

    @Override
    public void draw(Game game, Entity entity, Graphics2D g) {

    }
}
