package io.egg.server.replay.events.types;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.server.replay.Replay;
import io.egg.server.snapshots.ReplayPlayer;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class PlayerSwitchHeldItemEvent implements ReplayEvent<PlayerSwitchHeldItemEvent>, ReversibleEvent{
    public int entityId;
    public String material;
    public String oldMaterial;
    @Override
    public byte[] serialize() {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();
        bb.writeInt(entityId);
        bb.writeUTF(material);
        bb.writeUTF(oldMaterial);
        return bb.toByteArray();
    }

    @Override
    public PlayerSwitchHeldItemEvent fromBytes(byte[] data) {
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        entityId = bb.readInt();
        material = bb.readUTF();
        oldMaterial = bb.readUTF();
        return this;
    }

    @Override
    public void apply(InstanceContainer i, Replay r) {
        ReplayPlayer p = (ReplayPlayer) r.entities.get(entityId);
        try {
            Material m = Material.valueOf(material);
            p.setItemInMainHand(ItemStack.of(m));
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    @Override
    public void reverse(InstanceContainer i, Replay r) {
        ReplayPlayer p = (ReplayPlayer) r.entities.get(entityId);
        try {
            Material m = Material.valueOf(oldMaterial);
            p.setItemInMainHand(ItemStack.of(m));
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }
}
