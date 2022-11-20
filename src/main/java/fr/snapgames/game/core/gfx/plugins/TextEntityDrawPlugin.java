package fr.snapgames.game.core.gfx.plugins;

import fr.snapgames.game.core.entity.TextEntity;

import java.awt.*;

public class TextEntityDrawPlugin implements RendererPlugin<TextEntity> {
    @Override
    public Class<TextEntity> entityType() {
        return TextEntity.class;
    }

    @Override
    public void draw(Graphics2D g, TextEntity e) {
        g.rotate(e.rotation, e.size.x * 0.5, e.size.y * 0.5);
        g.setColor(e.color);
        g.setFont(e.font);
        int fh = g.getFontMetrics().getHeight();
        g.drawString(e.getText(), (int) e.position.x, (int) e.position.y + fh);
    }
}
