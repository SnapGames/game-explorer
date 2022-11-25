package fr.snapgames.game.core.entity.behaviors;

import java.awt.Graphics2D;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.behavior.Behavior;
import fr.snapgames.game.core.entity.Entity;

public class ScoreUpdateBehavior implements Behavior<Entity> {

    @Override
    public void update(Game game, Entity entity, double dt) {
        int score = (int) game.getEntities().get("player").getAttribute("score", 0);
        score += 1;
        game.getEntities().get("player").setAttribute("score", score);
    }

    @Override
    public void input(Game game, Entity entity) {
        // nothing to do for score update
    }

    @Override
    public void draw(Game game, Entity entity, Graphics2D g) {
        // nothing to do for score draw
    }

}
