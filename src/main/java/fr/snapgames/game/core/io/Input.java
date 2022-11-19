package fr.snapgames.game.core.io;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import fr.snapgames.game.Game;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Internal Input listener.
 *
 * @author Frédéric Delorme
 */
public class Input implements KeyListener {
    Game game;
    Map<Integer, KeyEvent> events = new ConcurrentHashMap<>();

    public Input(Game g) {
        this.game = g;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (Optional.ofNullable(game).isPresent()) {
            game.keyTyped(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        events.put(e.getKeyCode(), e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        events.remove(e.getKeyCode());
    }

    public boolean getKey(int code) {
        return (events.containsKey(code));
    }

}