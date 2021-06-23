package io.egg.server.replay;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.egg.server.replay.events.Tick;
import io.egg.server.replay.events.types.EntityMovementEvent;
import io.egg.server.replay.events.types.PlayerBreakBlockEvent;
import io.egg.server.replay.events.types.ReplayEvent;
import io.egg.server.snapshots.ReplayPlayer;
import io.egg.server.snapshots.Snapshot;
import io.egg.server.snapshots.SnapshotChunkLoader;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.scoreboard.Team;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.InflaterOutputStream;

public class Replay {
    public Snapshot initialState;
    // Entities by Old Id -> Entity
    public HashMap<Integer, Entity> entities = new HashMap<>();
    public HashMap<String, ReplayPlayer> players = new HashMap<>();
    public int currentTick = 0;
    public ArrayList<Tick> capturedTicks = new ArrayList<>();
    public boolean playing = false;
    public Direction direction = Direction.FORWARDS;
    InstanceContainer ic;
    public static Team VIEWERS_TEAM;

    public void setInstance(InstanceContainer i) {
        ic = i;
    }
    ReplayProfileDelegate cached = null;
    SnapshotChunkLoader cachedLoader = null;
    public ReplayProfileDelegate getDelegate() {
        if (cached != null) {
            return cached;
        }
        cached = new ReplayProfileDelegate(this);
        return cached;
    }

    public void parentTick() {
        if (playing && direction == Direction.FORWARDS) {
            nextTick();
        } else if (playing && direction == Direction.BACKWARDS) {
            previousTick();
        }
    }

    public SnapshotChunkLoader getChunkLoader() {
        if (cachedLoader != null) {
            return cachedLoader;
        }
        cachedLoader = new SnapshotChunkLoader(initialState);
        return cachedLoader;
    }


    public void nextTick() {
        if (capturedTicks.size() <= currentTick +1) {
            return;
        }
        currentTick++;
        capturedTicks.get(currentTick).apply(ic, this);
    }
    public void previousTick() {
        if (currentTick == 0) {
            return;
        }
        capturedTicks.get(currentTick).undo(ic, this);
        currentTick --;
        capturedTicks.get(currentTick).apply(ic, this);
    }



    public static Replay load(byte[] compressed) {
        byte[] data = decompress(compressed);
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        byte version = bb.readByte();
        System.out.println("Loading replay version 0x" + version);
        int snapshotLength = bb.readInt();
        int eventsLength = bb.readInt();
        byte[] snapshot = new byte[snapshotLength];
        bb.readFully(snapshot);
        byte[] events =  new byte[eventsLength];
        bb.readFully(events);
        Replay r = new Replay();
        r.initialState = Snapshot.loadSnapshot(snapshot, r);
        loadTicks(events, r);


        return r;
    }

    public enum Direction {
        FORWARDS,BACKWARDS
    }

    private static void loadTicks(byte[] data, Replay r) {
        /*
            format for event reels:
            int - count of ticks
            array of tick:
                int eventCount
                array of events:
                    String - Event Class Name
                    int - length of event data
                    byte[] - event data
         */
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        int tickCount = bb.readInt();
        System.out.println("Begin read " + tickCount + " ticks");
        for (int i = 0; i < tickCount; i++) {
            int eventCount = bb.readInt();
            Tick t = new Tick();
            if (eventCount < 20) {
                System.out.println("TICK " + eventCount + "has " + eventCount + " events");
            }
            for (int y = 0; y < eventCount; y++) {
                String eventName = bb.readUTF();
                int dataLen = bb.readInt();
                byte[] eventData = new byte[dataLen];
                bb.readFully(eventData);
                Class eventClass;
                try {
                    eventClass = Class.forName("io.egg.server.replay.events.types." + eventName);
                } catch (ClassNotFoundException e) {
                    System.out.println("Could not init event " + eventName);
                    continue;
                }
                ReplayEvent e;
                try {
                    e = (ReplayEvent) eventClass.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassCastException instantiationException) {
                    instantiationException.printStackTrace();
                    continue;
                }
                e.fromBytes(eventData);
                t.events.add(e);
               /* if (e instanceof EntityMovementEvent) {
                    if (((EntityMovementEvent) e).entityId == 147) {
                        System.out.println("147 moves on tick " + i);
                    }
                } else if (e instanceof PlayerBreakBlockEvent) {
                    System.out.println("break block on tick " + i);
                }*/
            }
            r.capturedTicks.add(t);
        }
    }



    public static byte[] decompress(byte[] in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InflaterOutputStream infl = new InflaterOutputStream(out);
            infl.write(in);
            infl.flush();
            infl.close();

            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(150);
            return null;
        }
    }

}
