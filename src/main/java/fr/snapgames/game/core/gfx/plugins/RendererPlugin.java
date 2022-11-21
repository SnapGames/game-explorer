package fr.snapgames.game.core.gfx.plugins;

import java.awt.*;

public interface RendererPlugin<T> {
    Class<T> entityType();

    void draw(Graphics2D g, T t);
}
