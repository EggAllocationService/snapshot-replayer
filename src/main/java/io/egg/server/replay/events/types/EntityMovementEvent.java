package io.egg.server.replay.events.types;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.server.replay.Replay;
import io.egg.server.snapshots.ReplayPlayer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.Position;

public class EntityMovementEvent implements ReplayEvent<EntityMovementEvent>, ReversibleEvent, LerpableEvent {
    public int entityId;
    public double x;
    public double y;
    public double z;
    public double pitch;
    public double yaw;
    public boolean hasFrom;
    public double fx;
    public double fy;
    public double fz;
    public double fpitch;
    public double fyaw;
    @Override
    public byte[] serialize() {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();
        bb.writeInt(entityId);
        bb.writeDouble(x);
        bb.writeDouble(y);
        bb.writeDouble(z);
        bb.writeDouble(pitch);
        bb.writeDouble(yaw);

        return bb.toByteArray();
    }

    @Override
    public EntityMovementEvent fromBytes(byte[] data) {
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        entityId = bb.readInt();
        x = bb.readDouble();
        y = bb.readDouble();
        z = bb.readDouble();
        pitch = bb.readDouble();
        yaw = bb.readDouble();
        if (yaw > 360) {
            yaw = yaw % 360;
        }
        hasFrom = bb.readBoolean();
        if(hasFrom) {
            fx = bb.readDouble();
            fy = bb.readDouble();
            fz = bb.readDouble();
            fpitch = bb.readDouble();
            fyaw = bb.readDouble();
            if (fyaw > 360) {
                fyaw = fyaw % 360;
            }
        }

        return this;
    }

    @Override
    public void apply(InstanceContainer i, Replay r) {
        Position newPos = new Position(x,y , z);

        newPos.setYaw((float)yaw);
        newPos.setPitch((float) pitch);
        if (!r.entities.containsKey(entityId)) {
            return;
        }
        r.entities.get(entityId).teleport(newPos);

    }

    @Override
    public void reverse(InstanceContainer i, Replay r) {
        if (!hasFrom) return;
        Entity y = r.entities.get(entityId);
        if (y == null) return;
        Position to = new Position(fx, fy, fz);
        to.setPitch((float) fpitch);
        to.setYaw((float) fyaw);
        y.teleport(to);
    }

    @Override
    public void lerp(InstanceContainer i, Replay r, double f) {
        double tx = lerpD(fx, x, f);
        double ty = lerpD(fy, y, f);
        double tz = lerpD(fz, z, f);
        double tpitch = lerpD(fpitch, pitch, f);
        double tyaw = lerpD(fyaw, yaw, f);
        Position to = new Position(tx, ty, tz);

        to.setPitch((float) tpitch);
        to.setYaw((float) tyaw);
        if (!r.entities.containsKey(entityId)) {
            return;
        }
        r.entities.get(entityId).teleport(to);


    }

    @Override
    public void lerpInverse(InstanceContainer i, Replay r, double f) {
        double tx = lerpD(x, fx, f);
        double ty = lerpD(y, fy, f);
        double tz = lerpD(z, fz, f);
        double tpitch = lerpD(pitch, fpitch, f);
        double tyaw = lerpD(yaw, fyaw, f);
        Position to = new Position(tx, ty, tz);
        to.setPitch((float) tpitch);
        to.setYaw((float) tyaw);
        if (!r.entities.containsKey(entityId)) {
            return;
        }
        r.entities.get(entityId).teleport(to);

    }

    public double lerpD(double a, double b, double f) {
        return a + f * (b - a);
    }
}
