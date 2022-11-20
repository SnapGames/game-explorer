package fr.snapgames.game.core.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.behaviors.Behavior;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.physic.Material;

import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * Entity manipulated by Game.
 *
 * @author Frédéric Delorme
 */
public class GameEntity extends Entity {
    public Vector2D position = new Vector2D(0, 0);
    public Vector2D speed = new Vector2D(0, 0);
    public Vector2D acceleration = new Vector2D(0, 0);
    public Vector2D size = new Vector2D(16, 16);
    public EntityType type = EntityType.RECTANGLE;
    public boolean stickToCamera = false;

    public Material material;

    public double rotation = 0.0;
    public List<Vector2D> forces = new ArrayList<>();
    public Color color = Color.RED;
    public BufferedImage image;

    /**
     * Create a new GameEntity with a name.
     *
     * @param name Name of the new entity.
     */
    public GameEntity(String name) {
        super(name);
        attributes.put("maxSpeed", 500.0);
        attributes.put("maxAcceleration", 300.0);
        attributes.put("mass", 1.0);
    }

    public void update(Game g, double dt) {
        for (Behavior b : behaviors) {
            b.update(g, this, dt);
        }
        if (!isStickToCamera()) {
            this.acceleration = this.acceleration.addAll(this.forces);
            this.acceleration = this.acceleration.multiply((double) attributes.get("mass"));

            this.acceleration.maximize((double) attributes.get("maxAcceleration"));

            this.speed = this.speed.add(this.acceleration.multiply(dt)).multiply(material.friction);
            this.speed.maximize((double) attributes.get("maxSpeed"));

            this.position = this.position.add(this.speed.multiply(dt));
            this.forces.clear();
        }
    }


    public GameEntity setPosition(Vector2D pos) {
        this.position = pos;
        return this;
    }

    public GameEntity stickToCamera(boolean flag) {
        this.stickToCamera = flag;
        return this;
    }

    public boolean isStickToCamera() {
        return stickToCamera;
    }

    public GameEntity setSize(Vector2D s) {
        this.size = s;
        return this;
    }

    public GameEntity setType(EntityType t) {
        this.type = t;
        return this;
    }

    public GameEntity setImage(BufferedImage i) {
        this.image = i;
        return this;
    }

    public GameEntity setSpeed(Vector2D speed) {
        this.speed = speed;
        return this;
    }


    public Collection<String> getDebugInfo() {
        List<String> ls = new ArrayList<>();
        ls.add(String.format("name:%s", name));
        ls.add(String.format("pos: %04.2f,%04.2f", this.position.x, this.position.y));
        ls.add(String.format("spd: %04.2f,%04.2f", this.speed.x, this.speed.y));
        ls.add(String.format("acc: %04.2f,%04.2f", this.acceleration.x, this.acceleration.y));
        ls.add(String.format("rot: %04.2f", this.rotation));
        return ls;
    }



    public GameEntity setColor(Color color) {
        this.color = color;
        return this;
    }



    public GameEntity setMaterial(Material m) {
        this.material = m;
        return this;
    }
}