package fr.snapgames.game.core.entity;

import fr.snapgames.game.core.math.Vector2D;

import java.awt.Dimension;

/**
 * Camera used to see/follow entity in game viewport.
 *
 * @author Frédéric Delorme
 */
public class CameraEntity extends GameEntity {
    public String target;
    public double tween = 0.0f;
    public Dimension viewport;


    public double scale = 1.0;

    public CameraEntity(String name) {
        super(name);
        position = new Vector2D(0, 0);
        target = null;
    }

    public CameraEntity setTarget(String t) {
        this.target = t;
        return this;
    }

    public CameraEntity setViewport(Dimension dim) {
        this.viewport = dim;
        return this;
    }

    public CameraEntity setRotation(double r) {
        this.rotation = r;
        return this;
    }

    public CameraEntity setTween(double tween) {
        this.tween = tween;
        return this;
    }
}