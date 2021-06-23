package io.egg.server.replay.events;

import io.egg.server.replay.Replay;
import io.egg.server.replay.events.types.LerpableEvent;
import io.egg.server.replay.events.types.ReplayEvent;
import io.egg.server.replay.events.types.ReversibleEvent;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;

public class Tick {
    public ArrayList<ReplayEvent> events = new ArrayList<>();
    public void apply(InstanceContainer i, Replay r) {
        for (ReplayEvent e : events) {
            e.apply(i, r);
        }
    }
    public void undo(InstanceContainer i, Replay r) {
        for (ReplayEvent e : events) {
            if (e instanceof ReversibleEvent) {
                ((ReversibleEvent) e).reverse(i, r);
            }
        }
    }
    public void applyInterpolated(InstanceContainer i, Replay r, double progress, boolean inverse) {
        for (ReplayEvent e: events) {
            if (e instanceof LerpableEvent) {
                if (inverse) {
                    ((LerpableEvent) e).lerpInverse(i, r, (float) progress);
                } else {
                    ((LerpableEvent) e).lerp(i, r, (float) progress);
                }
            }
        }
    }
}
