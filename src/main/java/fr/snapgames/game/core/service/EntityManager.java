package fr.snapgames.game.core.service;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * The Entity manager to ... manage entity.
 *
 * @author Frédéric Delorme
 * @since 2022
 */
public class EntityManager {
    private Game game;
    Map<String, Entity> entities = new HashMap<>();

    public EntityManager(Game g) {
        game = g;
    }

    public void add(Entity e) {
        entities.put(e.name, e);
    }

    public Entity get(String name) {
        return entities.get(name);
    }

    public Map<String, Entity> getEntities() {
        return entities;
    }
}
