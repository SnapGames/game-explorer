package fr.snapgames.game.core.entity.behaviors;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.behavior.Behavior;
import fr.snapgames.game.core.entity.Entity;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;

public class EnemyFollowerBehavior implements Behavior<Entity> {
    @Override
    public void input(Game g, Entity e) {

    }

    @Override
    public void draw(Game game, Entity e, Graphics2D g) {

    }

    @Override
    public void update(Game game, Entity entity, double dt) {
        // if player near this entity less than distance (attrDist),
        // a force (attrForce) is applied to entity to reach to player.
        GameEntity e = (GameEntity) entity;
        GameEntity p = (GameEntity) game.getEntities().get("player");
        double attrDist = (double) e.attributes.get("attraction.distance");
        double attrForce = (double) e.attributes.get("attraction.force");
        if (p.position.distance(e.position.add(p.size.multiply(0.5))) < attrDist) {
            Vector2D v = p.position.substract(e.position);
            e.forces.add(v.normalize().multiply(attrForce));
        }
    }
}