package fr.snapgames.game.core.entity;

import fr.snapgames.game.core.behavior.Behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {
    private static int index = 0;
    public int id = index++;
    public String name = "entity_" + id;

    public Map<String, Object> attributes = new HashMap<>();
    public List<Behavior> behaviors = new ArrayList<>();

    public List<Entity> child = new ArrayList<>();

    public Entity(String name) {
        this.name = name;
    }

    public Entity addBehavior(Behavior b) {
        this.behaviors.add(b);
        return this;
    }

    public Object getAttribute(String attrName, Object defaultValue) {
        return attributes.getOrDefault(attrName, defaultValue);
    }

    public Entity setAttribute(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    public Entity addChild(Entity c) {
        child.add(c);
        return this;
    }
}
