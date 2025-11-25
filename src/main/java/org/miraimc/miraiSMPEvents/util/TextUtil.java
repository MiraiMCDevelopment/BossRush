package org.miraimc.miraiSMPEvents.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@UtilityClass
public final class TextUtil {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    public static Component translate(String string) {
        return LEGACY.deserialize(string);
    }

    public static String toLegacy(Component component) {
        return LEGACY.serialize(component);
    }
}
