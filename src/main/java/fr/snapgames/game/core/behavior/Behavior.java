package fr.snapgames.game.core.behavior;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.Entity;

import java.awt.*;

public interface Behavior<T> {
    void update(Game game, T entity, double dt);

    void input(Game game, T entity);

    void draw(Game game, T entity, Graphics2D g);
}