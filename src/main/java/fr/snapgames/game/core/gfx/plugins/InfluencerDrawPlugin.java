package fr.snapgames.game.core.gfx.plugins;

import fr.snapgames.game.core.math.physic.Influencer;

import java.awt.*;
import java.util.Optional;

/**
 * @author : M313104
 * @mailto : buy@mail.com
 * @created : 23/11/2022
 **/
public class InfluencerDrawPlugin implements RendererPlugin<Influencer> {
    @Override
    public Class<Influencer> entityType() {
        return Influencer.class;
    }

    @Override
    public void draw(Graphics2D g, Influencer e) {
        g.rotate(e.rotation, e.size.x * 0.5, e.size.y * 0.5);
        g.setColor(e.color);
        g.fillRect((int)
                e.position.x, (int) e.position.y, (int)
                e.size.x, (int) e.size.y);
    }
}
