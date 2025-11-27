package org.miraimc.bossRush.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeFormatUtil {

    public static String formatMillis(long millis) {
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis / (1000 * 60)) % 60;
        long seconds = (millis / 1000) % 60;
        long ms = millis % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, ms);
    }
}
