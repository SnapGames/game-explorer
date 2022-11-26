package fr.snapgames.game.core.entity.behaviors;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.behavior.Behavior;
import fr.snapgames.game.core.entity.Entity;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class EnemyFollowerBehavior implements Behavior<Entity> {
    private boolean collide;

    @Override
    public void input(Game g, Entity e) {

    }

    @Override
    public void draw(Game game, Entity entity, Graphics2D g) {
        // display specific debug information.
        if (game.getDebug() > 2) {
            GameEntity e = (GameEntity) entity;
            GameEntity p = (GameEntity) game.getEntities().get("player");

            if ((double) e.getAttribute("attraction.distance", 0.0) > 0.0) {
                g.setColor(Color.LIGHT_GRAY);
                double d = (double) e.getAttribute("attraction.distance", 0.0);
                drawDistanceArea(g, e, d, new float[]{2f, 0f, 2f});
                g.setColor(Color.DARK_GRAY);
                double r = (double) e.getAttribute("attraction.release", 0.0);
                drawDistanceArea(g, e, r, new float[]{1f, 0f, 1f});
            }
            if (collide) {
                double s = (double) e.getAttribute("attraction.force", 0.0);
                g.setColor(Color.GRAY);
                g.setStroke(new BasicStroke((int) (s / 4.0) + 1));
                g.drawLine(
                        (int) (e.position.x + (e.size.x * 0.5)),
                        (int) (e.position.y + (e.size.y * 0.5)),
                        (int) (p.position.x + (p.size.x * 0.5)),
                        (int) (p.position.y + (p.size.y * 0.5)));
                g.setStroke(new BasicStroke(1));
            }
        }
    }

    private void drawDistanceArea(Graphics2D g, GameEntity e, double d, float[] dash1) {
        BasicStroke bs1 = new BasicStroke(1,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND,
                1.0f,
                dash1,
                2f);
        g.setStroke(bs1);
        g.draw(new Ellipse2D.Double(
                e.position.x - (d - e.size.x) * 0.5,
                e.position.y - (d - e.size.y) * 0.5,
                d, d));
        g.setStroke(new BasicStroke());
    }

    @Override
    public void update(Game game, Entity entity, double dt) {
        // if player near this entity less than distance (attrDist),
        // a force (attrForce) is applied to entity to reach to player.
        GameEntity e = (GameEntity) entity;
        GameEntity p = (GameEntity) game.getEntities().get("player");
        double attrDist = (double) e.attributes.get("attraction.distance");
        double attrRelease = (double) e.attributes.get("attraction.release");
        double attrForce = (double) e.attributes.get("attraction.force");
        double distance = p.position.add(p.size.multiply(-0.5))
                .distance(
                        e.position.add(e.size.multiply(-0.5)));
        if (distance < attrDist) {
            this.collide = true;
            Vector2D v = p.position.substract(e.position);
            e.forces.add(v.normalize().multiply(attrForce));
        }
        if (distance > attrRelease) {
            this.collide = false;
        }
    }
}