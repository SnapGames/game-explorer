package fr.snapgames.game.core.math.physic;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.behaviors.Behavior;

/**
 * The {@link PhysicEngine} is only the place where entities ({@link GameEntity}
 * are updated to follow some basic newton's laws.
 *
 * @author Frédéric Delorme
 * @since 0.0.1
 */
public class PhysicEngine {

    World world;

    public PhysicEngine(Game g) {

    }

    public PhysicEngine setWorld(World w) {
        this.world = w;
        return this;
    }

    public void update(Game g, double elapsed) {
        g.getEntities().values().forEach(e -> {
            updateEntity(g, e, elapsed);
            constrainEntityToWorld(world, e);
            for (Behavior b : g.getCurrentCamera().behaviors) {
                b.update(g, g.getCurrentCamera(), elapsed);
            }
        });
    }

    public void updateEntity(Game g, GameEntity e, double elapsed) {
        e.forces.add(world.getGravity());
        if (!e.isStickToCamera()) {
            e.acceleration = e.acceleration.addAll(e.forces);
            e.acceleration = e.acceleration.multiply((double) e.attributes.get("mass"));

            e.acceleration.maximize((double) e.attributes.get("maxAcceleration"));

            e.speed = e.speed
                    .add(e.acceleration.multiply(elapsed))
                    .multiply(e.material.friction);
            e.speed.maximize((double) e.attributes.get("maxSpeed"));

            e.position = e.position.add(e.speed.multiply(elapsed));
            e.forces.clear();
        }
        for (Behavior b : e.behaviors) {
            b.update(g, e, elapsed);
        }
    }

    /**
     * Constrain the GameEntity ge to stay in the world play area.
     *
     * @param world the defne World for the Game
     * @param ge    the GameEntity to be checked against world's play area
     *              constrains
     * @see GameEntity
     * @see World
     */
    private void constrainEntityToWorld(World world, GameEntity ge) {
        if (world.isNotContaining(ge)) {
            if (ge.position.x + ge.size.x > world.getPlayArea().width) {
                ge.position.x = world.getPlayArea().width - ge.size.x;
            }
            if (ge.position.x < 0) {
                ge.position.x = 0;
            }
            if (ge.position.y + ge.size.y > world.getPlayArea().height) {
                ge.position.y = world.getPlayArea().height - ge.size.y;
            }
            if (ge.position.y < 0) {
                ge.position.y = 0;
            }
            ge.speed = ge.speed.multiply(-ge.material.elasticity);
        }
    }

    public World getWorld() {
        return world;
    }
}
