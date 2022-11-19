package fr.snapgames.game.core.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.behaviors.Behavior;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

/**
 * Entity manipulated by Game.
 *
 * @author Frédéric Delorme
 */
public class GameEntity {
    public String name = "noname";
    public Vector2D position = new Vector2D(0, 0);
    public Vector2D speed = new Vector2D(0, 0);
    public Vector2D acceleration = new Vector2D(0, 0);
    public Vector2D size = new Vector2D(16, 16);
    public EntityType type = EntityType.RECTANGLE;
    public boolean stickToCamera = false;
    public double elasticity = 1.0;
    public double roughness = 1.0;
    public double rotation = 0.0;
    public List<Vector2D> forces = new ArrayList<>();
    public Color color = Color.RED;
    public Map<String, Object> attributes = new HashMap<>();
    public List<Behavior> behaviors = new ArrayList<>();
    public BufferedImage image;

    /**
     * Create a new GameEntity with a name.
     *
     * @param name Name of the new entity.
     */
    public GameEntity(String name) {
        this.name = name;
        attributes.put("maxSpeed", 8.0);
        attributes.put("maxAcceleration", 3.0);
        attributes.put("mass", 10.0);
    }

    public void update(Game g, double dt) {
        for (Behavior b : behaviors) {
            b.update(g, this, dt);
        }
        if (!isStickToCamera()) {
            this.acceleration = this.acceleration.addAll(this.forces);
            this.acceleration = this.acceleration.multiply((double) attributes.get("mass"));

            this.acceleration.maximize((double) attributes.get("maxAcceleration"));

            this.speed = this.speed.add(this.acceleration.multiply(dt)).multiply(roughness);
            this.speed.maximize((double) attributes.get("maxSpeed"));

            this.position = this.position.add(this.speed.multiply(dt));
            this.forces.clear();
        }
    }

    public void draw(Graphics2D g) {
        switch (type) {
            case IMAGE:
                if (Optional.ofNullable(image).isPresent()) {
                    boolean direction = speed.x > 0;
                    if (direction) {
                        g.drawImage(image, (int) position.x, (int) position.y, null);
                    } else {
                        g.drawImage(image, (int) (position.x + size.x), (int) position.y, (int) -size.x, (int) size.y,
                                null);
                    }
                }
                break;
            case RECTANGLE:
                g.setColor(color);
                g.fillRect((int) position.x, (int) position.y, (int) size.x, (int) size.y);
                break;
            case CIRCLE:
                g.setColor(color);
                g.setPaint(color);
                g.fill(new Ellipse2D.Double(position.x, position.y, size.x, size.y));
                break;
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

    public GameEntity setRoughness(double r) {
        this.roughness = r;
        return this;
    }

    public GameEntity setElasticity(double e) {
        this.elasticity = e;
        return this;
    }

    public Collection<String> getDebugInfo() {
        List<String> ls = new ArrayList<>();
        ls.add(String.format("name:%s", name));
        ls.add(String.format("pos: %04.2f,%04.2f", this.position.x, this.position.y));
        ls.add(String.format("spd: %04.2f,%04.2f", this.speed.x, this.speed.y));
        ls.add(String.format("acc: %04.2f,%04.2f", this.acceleration.x, this.acceleration.y));
        return ls;
    }

    public GameEntity setAttribute(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    public GameEntity setColor(Color color) {
        this.color = color;
        return this;
    }

    public GameEntity addBehavior(Behavior b) {
        this.behaviors.add(b);
        return this;
    }

    public Object getAttribute(String attrName, Object defaultValue) {
        return attributes.getOrDefault(attrName, defaultValue);
    }
}