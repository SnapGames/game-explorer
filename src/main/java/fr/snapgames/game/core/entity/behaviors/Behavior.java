package fr.snapgames.game.core.entity.behaviors;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.GameEntity;

import java.awt.Graphics2D;

public interface Behavior {
    void update(Game game, GameEntity entity, double dt);

    void input(Game game, GameEntity entity);

    void draw(Game game, GameEntity entity, Graphics2D g);
}