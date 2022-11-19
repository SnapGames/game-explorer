package fr.snapgames.game.core.entity.behaviors;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.Graphics2D;

public class EnemyFollowerBehavior implements Behavior {
    @Override
    public void input(Game g, GameEntity e) {

    }

    @Override
    public void draw(Game game, GameEntity e, Graphics2D g) {

    }

    @Override
    public void update(Game game, GameEntity entity, double dt) {
        // if player near this entity less than distance (attrDist),
        // a force (attrForce) is applied to entity to reach to player.
        GameEntity p = game.getEntities().get("player");
        double attrDist = (double) entity.attributes.get("attractionDistance");
        double attrForce = (double) entity.attributes.get("attractionForce");
        if (p.position.distance(entity.position.add(p.size.multiply(0.5))) < attrDist) {
            Vector2D v = p.position.substract(entity.position);
            entity.forces.add(v.normalize().multiply(attrForce));
        }
    }
}