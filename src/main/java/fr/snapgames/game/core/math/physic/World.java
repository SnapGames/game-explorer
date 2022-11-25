package fr.snapgames.game.core.math.physic;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.math.Vector2D;

public class World {
    private Dimension playArea;
    private Vector2D gravity;
    private Material material;
    private List<Influencer> influencers = new ArrayList<>();

    public World(Dimension area, Vector2D gravity) {
        this.playArea = area;
        this.gravity = gravity;
        this.material = new Material("worldDefaultMaterial", 1.0, 1.0, 1.0);
    }

    public World add(Influencer i) {
        this.influencers.add(i);
        return this;
    }

    public Vector2D getGravity() {
        return this.gravity;
    }

    public Dimension getPlayArea() {
        return playArea;
    }

    public boolean isNotContaining(GameEntity ge) {
        return ge.position.x < 0
                || ge.position.x + ge.size.x > playArea.width
                || ge.position.y < 0
                || ge.position.y + ge.size.y > playArea.height;
    }

    public Collection<Influencer> getCollidingInfluencerWith(GameEntity e) {
        return influencers.stream().filter(i -> e.box.intersects((Rectangle2D) i.box)).collect(Collectors.toList());
    }

    public Material getMaterial() {
        return material;
    }

    public World setMaterial(Material mat) {
        this.material = mat;
        return this;
    }
}
