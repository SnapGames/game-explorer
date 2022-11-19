package fr.snapgames.game.core.utils;

import java.util.ResourceBundle;

public class I18n {
    private static final ResourceBundle messages = ResourceBundle.getBundle("i18n.messages");

    public static String get(String key) {
        return messages.getString(key);
    }

    public static String get(String key, Object... args) {
        return String.format(messages.getString(key), args);
    }
}