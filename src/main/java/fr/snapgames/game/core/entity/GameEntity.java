package fr.snapgames.game.core.entity;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.physic.Material;

import java.awt.image.BufferedImage;

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
    public double mass;

    public Shape box;

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

    public GameEntity setPosition(Vector2D pos) {
        this.position = pos;
        updateBox();
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
        updateBox();
        return this;
    }

    public GameEntity setType(EntityType t) {
        this.type = t;
        return this;
    }

    public GameEntity setImage(BufferedImage i) {
        this.image = i;
        this.type = EntityType.IMAGE;
        return this;
    }

    public GameEntity setSpeed(Vector2D speed) {
        this.speed = speed;
        return this;
    }

    public Collection<String> getDebugInfo() {
        List<String> ls = new ArrayList<>();
        ls.add(String.format("id:%d", id));
        ls.add(String.format("name:%s", name));
        ls.add(String.format("type:%s", type));
        ls.add(String.format("pos: %04.2f,%04.2f", this.position.x, this.position.y));
        ls.add(String.format("spd: %04.2f,%04.2f", this.speed.x, this.speed.y));
        ls.add(String.format("acc: %04.2f,%04.2f", this.acceleration.x, this.acceleration.y));
        ls.add(String.format("rot: %04.2f", this.rotation));
        ls.add(String.format("mass: %04.2f", this.mass));
        ls.add(String.format("mat: %s", this.material));
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

    public GameEntity setMass(double m) {
        this.mass = m;
        return this;
    }

    public void updateBox() {
        switch (type) {
            case CIRCLE -> {
                this.box = new Ellipse2D.Double(position.x, position.y, size.x, size.y);
            }
            case RECTANGLE, IMAGE -> {
                this.box = new Rectangle2D.Double(position.x, position.y, size.x, size.y);
            }
        }
    }

    public GameEntity addForces(List<Vector2D> fs) {
        forces.addAll(fs);
        return this;
    }

    public GameEntity addForce(Vector2D f) {
        forces.add(f);
        return this;
    }

    public boolean isAttributeExist(String materialName) {
        return attributes.containsKey(materialName);
    }
}