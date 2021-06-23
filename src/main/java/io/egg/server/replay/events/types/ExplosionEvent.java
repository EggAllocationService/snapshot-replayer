package io.egg.server.replay.events.types;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.server.replay.Replay;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import net.minestom.server.utils.BlockPosition;

import java.util.ArrayList;

public class ExplosionEvent implements ReplayEvent<ExplosionEvent>, ReversibleEvent{
    public ArrayList<int[]> toBreak;
    public ArrayList<Short> cache;
    boolean hasCached = false;
    public int x;
    public int y;
    public int z;
    @Override
    public byte[] serialize() {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();
        bb.writeInt(x);
        bb.writeInt(y);
        bb.writeInt(z);
        bb.writeInt(toBreak.size());
        for (int[] l : toBreak) {
            encodeLocation(l, bb);
        }
        return bb.toByteArray();
    }
    private void encodeLocation(int[] l, ByteArrayDataOutput bb) {
        bb.writeInt(l[0]);
        bb.writeInt(l[1]);
        bb.writeInt(l[2]);
    }

    @Override
    public ExplosionEvent fromBytes(byte[] data) {
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        x = bb.readInt();
        y = bb.readInt();
        z = bb.readInt();
        int count = bb.readInt();
        cache = new ArrayList<>(count);
        toBreak = new ArrayList<>();
        for (int i =0; i < count; i++) {
            int[] loc = new int[3];
            loc[0] = bb.readInt();
            loc[1] = bb.readInt();
            loc[2] = bb.readInt();
            toBreak.add(loc);
        }
        return this;
    }

    @Override
    public void apply(InstanceContainer i, Replay r) {
        for (int d = 0; d < toBreak.size(); d++) {
            int[] pos = toBreak.get(d);
            BlockPosition e = new BlockPosition(pos[0], pos[1], pos[2]);
            if (!hasCached) {
                cache.add(i.getBlock(e).getBlockId());
            }
            i.setBlock(e, Block.AIR);

        }
        hasCached = true;
        ParticlePacket pp = ParticleCreator.createParticlePacket(
                Particle.EXPLOSION,
                (float) x,
                (float) y,
                (float) z,
                1.0f,
                1.0f,
                1.0f,
                10
        );
        BlockPosition center = new BlockPosition(x, y, z);
        i.getChunkAt(center).sendPacketsToViewers(pp);

    }

    @Override
    public void reverse(InstanceContainer i, Replay r) {
        if (!hasCached) return;
        for (int d = 0; d < cache.size(); d++) {
            int[] pos = toBreak.get(d);
            BlockPosition target = new BlockPosition(pos[0], pos[1], pos[2]);
            i.setBlock(target, Block.fromStateId(cache.get(d)));

        }
    }
}
