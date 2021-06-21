package io.egg.server.replay.events.types;

import io.egg.server.replay.Replay;
import net.minestom.server.instance.InstanceContainer;

public interface ReplayEvent<SELF> {
    byte[] serialize();
    SELF fromBytes(byte[] data);
    void apply(InstanceContainer i, Replay r);
}
