package dev.basicallycoding.magicitems.systems;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.InventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class BootsOfSpeedInventorySystem extends EntityEventSystem<EntityStore, InventoryChangeEvent> {
    private static final String BOOTS_ID = "BootsOfSpeed";
    private static final String EFFECT_ID = "BootsOfSpeed_SpeedBuff";
    private static final int EFFECT_SLOT = 1;

    public BootsOfSpeedInventorySystem() {
        super(InventoryChangeEvent.class);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }

    @Override
    public void handle(int index,
                       @Nonnull ArchetypeChunk<EntityStore> chunk,
                       @Nonnull Store<EntityStore> store,
                       @Nonnull CommandBuffer<EntityStore> commandBuffer,
                       @Nonnull InventoryChangeEvent event) {
        ItemContainer changed = event.getItemContainer();
        if (changed == null) return;

        if (changed.getCapacity() != Inventory.DEFAULT_ARMOR_CAPACITY) return;

        Ref<EntityStore> entityRef = chunk.getReferenceTo(index);
        Player player = commandBuffer.getComponent(entityRef, Player.getComponentType());
        if (player == null) return;

        boolean bootsInArmor = false;
        short capacity = changed.getCapacity();
        for (short i = 0; i < capacity; i++) {
            ItemStack stack = changed.getItemStack(i);
            if (stack != null && BOOTS_ID.equals(stack.getItemId())) {
                bootsInArmor = true;
                break;
            }
        }

        EffectControllerComponent ec = commandBuffer.ensureAndGetComponent(
            entityRef, EffectControllerComponent.getComponentType());

        boolean hasEffect = ec.hasEffect(EFFECT_SLOT);

        if (bootsInArmor && !hasEffect) {
            EntityEffect effect = EntityEffect.getAssetMap().getAsset(
                EntityEffect.getAssetMap().getIndex(EFFECT_ID));
            if (effect != null) {
                ec.addInfiniteEffect(entityRef, EFFECT_SLOT, effect, commandBuffer);
            }
        } else if (!bootsInArmor && hasEffect) {
            ec.removeEffect(entityRef, EFFECT_SLOT, commandBuffer);
        }
    }
}
