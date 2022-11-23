package fr.snapgames.game.core.entity;

import java.awt.Font;
import java.util.Collection;

public class TextEntity extends GameEntity {

    public String text;
    public Font font;
    private int value;
    private GameEntity entityValueSource;
    private String attributeNameSource;
    private Object defaultValueSource;

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

    @Override
    public Collection<String> getDebugInfo() {
        Collection<String> l = super.getDebugInfo();
        l.add(String.format("txt:%s", text));
        return l;
    }

    public TextEntity setValue(GameEntity entitySource, String attrName, Object defaultValue) {
        this.entityValueSource = entitySource;
        this.attributeNameSource = attrName;
        this.defaultValueSource = defaultValue;
        return this;
    }

    public Object getValue() {
        return entityValueSource.getAttribute(attributeNameSource, defaultValueSource);
    }

    public String getText() {
        if (text.contains("%")) {
            return String.format(text, getValue());
        } else {
            return text;
        }
    }
}