package fr.snapgames.game.core.utils;

import java.util.concurrent.TimeUnit;

public class Converters {

    private Converters() {
        // just to prevent utilisties class instantiation.
    }

    public static String formatTime(long millis) {
        long seconds = Math.round((double) millis / 1000);
        long hours = TimeUnit.SECONDS.toHours(seconds);
        if (hours > 0)
            seconds -= TimeUnit.HOURS.toSeconds(hours);
        long minutes = seconds > 0 ? TimeUnit.SECONDS.toMinutes(seconds) : 0;
        if (minutes > 0)
            seconds -= TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
