package fr.snapgames.game.core.entity.behaviors;

public class Entity {
    private static int index = 0;
    public int id = index++;
    public String name = "entity_" + id;

    public Entity(String name) {
        this.name = name;
    }
}
