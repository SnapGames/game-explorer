package fr.snapgames.game.core.gfx.plugins;

import fr.snapgames.game.core.entity.GameEntity;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Optional;

public class GameEntityDrawPlugin implements RendererPlugin<GameEntity> {


    @Override
    public Class<GameEntity> entityType() {
        return GameEntity.class;
    }

    @Override
    public void draw(Graphics2D g, GameEntity e) {
        g.rotate(e.rotation, e.size.x * 0.5, e.size.y * 0.5);
        switch (e.type) {
            case IMAGE:
                if (Optional.ofNullable(e.image).isPresent()) {
                    boolean direction = e.speed.x > 0;
                    if (direction) {
                        g.drawImage(e.image,
                                (int) e.position.x, (int) e.position.y,
                                null);
                    } else {
                        g.drawImage(e.image,
                                (int) (e.position.x + e.size.x), (int) e.position.y,
                                (int) -e.size.x, (int) e.size.y,
                                null);
                    }
                }
                break;
            case RECTANGLE:
                g.setColor(e.color);
                g.fillRect((int)
                        e.position.x, (int) e.position.y, (int)
                        e.size.x, (int) e.size.y);
                break;
            case CIRCLE:
                g.setColor(e.color);
                g.setPaint(e.color);
                g.fill(new Ellipse2D.Double(
                        e.position.x, e.position.y,
                        e.size.x, e.size.y));
                break;
        }
    }
}
