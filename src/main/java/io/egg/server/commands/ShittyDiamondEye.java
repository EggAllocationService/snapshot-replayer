package io.egg.server.commands;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.item.EyeOfEnderMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ShittyDiamondEye extends EntityCreature {
    public ShittyDiamondEye() {
        super(EntityType.EYE_OF_ENDER);
        ((EyeOfEnderMeta) getEntityMeta()).setItem(ItemStack.of(Material.DIAMOND_BLOCK));
    }
}
