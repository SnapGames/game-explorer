package fr.snapgames.game.core.entity;

import fr.snapgames.game.core.entity.behaviors.Behavior;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Camera used to see/follow entity in game viewport.
 *
 * @author Frédéric Delorme
 */
public class Camera extends GameEntity {
    public GameEntity target;
    public double rotation = 0.0f, tween = 0.0f;
    public Dimension viewport;


    public double scale = 1.0;

    public Camera(String name) {
        super(name);
        position = new Vector2D(0, 0);
        target = null;
    }

    public Camera setTarget(GameEntity t) {
        this.target = t;
        return this;
    }

    public Camera setViewport(Dimension dim) {
        this.viewport = dim;
        return this;
    }

    public Camera setRotation(double r) {
        this.rotation = r;
        return this;
    }

    public Camera setTween(double tween) {
        this.tween = tween;
        return this;
    }

    public void preDraw(Graphics2D g) {
        if (rotation != 0.0) {
            g.rotate(-rotation, viewport.width * 0.5, viewport.height * 0.5);
        }
        g.translate(-position.x, -position.y);
    }

    public void postDraw(Graphics2D g) {
        g.translate(position.x, position.y);
        if (rotation != 0.0) {
            g.rotate(rotation, viewport.width * 0.5, viewport.height * 0.5);
        }
    }
}