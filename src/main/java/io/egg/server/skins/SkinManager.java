package io.egg.server.skins;

import net.minestom.server.entity.PlayerSkin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class SkinManager {
    static HashMap<String, PlayerSkin> cache = new HashMap<>();

    public static PlayerSkin get(UUID id) {
        return get(id.toString().replaceAll("-", ""));
    }
    public static PlayerSkin get(String id) {
        Logger.getAnonymousLogger().info(id);
        if (cache.containsKey(id)) {
            return cache.get(id);
        } else {
            //fetch from mogang
            cache.put(id, PlayerSkin.fromUuid(id));
            return cache.get(id);
        }
    }
    public static PlayerSkin getName(String id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        } else {
            if (id == null) {
                return PlayerSkin.fromUsername("Steve");
            }
            //fetch from mogang
            if (id.contains("[")) {
                return null;
            }
            cache.put(id, PlayerSkin.fromUsername(id));
            return cache.get(id);
        }
    }
}
