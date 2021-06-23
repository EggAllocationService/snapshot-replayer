package io.egg.server.replay.events.types;

import io.egg.server.replay.Replay;
import net.minestom.server.instance.InstanceContainer;

public interface LerpableEvent {
    void lerp(InstanceContainer i, Replay r, double f);
    void lerpInverse(InstanceContainer i, Replay r, double f);
}
