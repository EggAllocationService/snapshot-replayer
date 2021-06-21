package io.egg.server.replay.events.types;

import io.egg.server.replay.Replay;
import net.minestom.server.instance.InstanceContainer;

public interface ReversibleEvent {
    void reverse(InstanceContainer i, Replay r);
}
