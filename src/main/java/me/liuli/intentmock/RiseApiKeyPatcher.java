package me.liuli.intentmock;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class RiseApiKeyPatcher {
    public static void patch() {
        try {
            Class<?> klass = Class.forName("net.minecraft.client.main.Main");
            // find all string fields
            for (Field field : klass.getDeclaredFields()) {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    if(Modifier.isStatic(field.getModifiers())) {
                        String value = (String) field.get(null);
                        if (value.isEmpty()) {
                            field.set(null, "rise_is_trash");
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
