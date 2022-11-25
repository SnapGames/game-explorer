package fr.snapgames.game.core.math.physic;

import fr.snapgames.game.core.entity.GameEntity;

public class Influencer extends GameEntity {

    private World world;

    public Influencer(String name) {
        super(name);
    }

    public World getWorld() {
        return world;
    }

    public Influencer setWorld(World w) {
        this.world = w;
        return this;
    }

}
