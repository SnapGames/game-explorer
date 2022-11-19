package fr.snapgames.game.core.entity;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Collection;

public class TextEntity extends GameEntity {

    public String text;
    public Font font;

    /**
     * Create a new TextEntity with a name.
     *
     * @param name Name of the new entity.
     */
    public TextEntity(String name) {
        super(name);
    }

    public TextEntity setText(String text) {
        this.text = text;
        return this;
    }

    public TextEntity setFont(Font font) {
        this.font = font;
        return this;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setFont(font);
        g.drawString(text, (int) position.x, (int) position.y);
    }

    @Override
    public Collection<String> getDebugInfo() {
        Collection<String> l = super.getDebugInfo();
        l.add(String.format("txt:%s", text));
        return l;
    }
}