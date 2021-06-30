package io.egg.server.replay;

import io.egg.server.instances.InstanceManager;
import io.egg.server.snapshots.Snapshot;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.InstanceContainer;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ReplayManager {
    public static HashMap<String, Replay> replayHashMap = new HashMap<>();
    public static InstanceContainer create(String name, byte[] data) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Replay r = Replay.load(name, data);
        InstanceContainer ic = InstanceManager.get().spawn(name, r.getDelegate(), r.getChunkLoader());;
        r.setInstance(ic);
        replayHashMap.put(name, r);

        return ic;
    }
}
