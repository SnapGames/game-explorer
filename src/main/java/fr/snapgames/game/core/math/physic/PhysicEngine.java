package fr.snapgames.game.core.math.physic;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.behaviors.Behavior;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;
import java.util.Collection;
import java.util.Optional;

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
        Configuration config = g.getConfiguration();
        int playAreaWidth = config.getInteger("game.world.width", 800);
        int playAreaHeight = config.getInteger("game.world.height", 800);
        this.world = new World(
                new Dimension(playAreaWidth, playAreaHeight),
                config.getVector2D("game.world.gravity", new Vector2D()).multiply(-10.0));
        Material worldMat = config.getMaterial("game.world.material",
                new Material("defaultWorldMaterial", 1.0, 1.0, 1.0));
        this.world.setMaterial(worldMat);
    }

    public PhysicEngine setWorld(World w) {
        this.world = w;
        return this;
    }

    public void update(Game g, double elapsed) {
        g.getEntities().values().stream()
                .filter(e -> !(e instanceof Influencer))
                .forEach(e -> {
                    updateEntity(g, (GameEntity) e, elapsed);
                    constrainEntityToWorld(world, (GameEntity) e);

                });
        for (Behavior b : g.getCurrentCamera().behaviors) {
            b.update(g, g.getCurrentCamera(), elapsed);
        }
    }

    public void updateEntity(Game g, GameEntity e, double elapsed) {
        e.forces.add(world.getGravity().multiply(2.0));
        if (!e.isStickToCamera()) {
            Material mWorld = addInfluencersEffects(e, world.getCollidingInfluencerWith(e));

            double friction = e.material.friction * (mWorld != null ? mWorld.friction : 1.0);
            double density = e.material.density * (mWorld != null ? mWorld.density : 1.0);

            e.acceleration = e.acceleration.addAll(e.forces)
                    .multiply((double) e.mass * density);

            e.acceleration.maximize((double) e.attributes.get("maxAcceleration"));

            e.speed = e.speed
                    .add(e.acceleration.multiply(elapsed))
                    .multiply(friction);
            e.speed.maximize((double) e.attributes.get("maxSpeed"));

            e.position = e.position.add(e.speed.multiply(elapsed));
            e.forces.clear();
        }
        for (Behavior b : e.behaviors) {
            b.update(g, e, elapsed);
        }
        e.updateBox();
        e.child.forEach(c -> updateEntity(g, e, elapsed));
    }

    private Material addInfluencersEffects(GameEntity e, Collection<Influencer> collidingInfluencerWith) {
        Material m = null;
        for (Influencer i : collidingInfluencerWith) {
            e.addForces(i.forces);
            if (Optional.ofNullable(i.material).isPresent()) {
                m = i.material;
            }
        }
        return m;
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
            if (ge.material != null) {
                ge.speed = ge.speed.multiply(-ge.material.elasticity);
            }
        } else {
            if (world.getMaterial() != null) {
                ge.speed = ge.speed.multiply(world.getMaterial().friction);
            }
        }
    }

    public World getWorld() {
        return world;
    }
}
