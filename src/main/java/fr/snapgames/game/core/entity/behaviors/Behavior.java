package fr.snapgames.game.core.entity.behaviors;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.Entity;

import java.awt.*;

public interface Behavior {
    void update(Game game, Entity entity, double dt);

    void input(Game game, Entity entity);

    void draw(Game game, Entity entity, Graphics2D g);
}