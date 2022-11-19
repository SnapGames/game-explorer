package fr.snapgames.game.core.math.physic;

import fr.snapgames.game.core.entity.GameEntity;

public class Material {
    public String name = "default";
    public double density = 1.0;
    public double elasticity = 1.0;
    public double friction = 1.0;

    public Material(String name, double d, double e, double f) {
        this.name = name;
        this.density = d;
        this.elasticity = e;
        this.friction = f;
    }


    public Material setFriction(double f) {
        this.friction = f;
        return this;
    }

    public Material setElasticity(double e) {
        this.elasticity = e;
        return this;
    }
}
