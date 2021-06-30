package io.egg.server.snapshots;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.metadata.ProjectileMeta;
import net.minestom.server.entity.metadata.arrow.ArrowMeta;
import net.minestom.server.entity.metadata.other.PrimedTntMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ReplayEntity extends EntityCreature {
    public SEntityInfo data;
    public ReplayEntity(SEntityInfo info) {
        super(EntityType.valueOf(info.type));
        data = info;
        setNoGravity(true);
        //setGravity(0, 0, 0);
        if (this.getEntityType() == EntityType.TNT) {
            ((PrimedTntMeta) getEntityMeta()).setFuseTime(80);
        }

    }
    @Override
    public void refreshPosition(@NotNull Position position) {
        if (currentChunk == null) {
            return;
        }
        super.refreshPosition(position);
    }
    public void init(Instance target) {
        Position startingPos = new Position();
        startingPos.setX(data.x);
        startingPos.setY(data.y);
        startingPos.setZ(data.z);
        startingPos.setPitch((float) data.pitch);
        startingPos.setYaw((float) data.yaw);
        setInstance(target, startingPos);
        if (data.name != null) {
            setCustomName(Component.text(data.name));
            setCustomNameVisible(true);
        }
        target.loadChunk(startingPos, chunk -> {
            currentChunk = chunk;
        });

    }
}
