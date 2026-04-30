package dev.basicallycoding.magicitems.systems;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.InventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class BootsOfSpeedInventorySystem extends EntityEventSystem<EntityStore, InventoryChangeEvent> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String BOOTS_ID = "boots_of_speed";
    private static final String EFFECT_ID = "BootsOfSpeed_SpeedBuff";
    private static final int EFFECT_SLOT = 1;

    public BootsOfSpeedInventorySystem() {
        super(InventoryChangeEvent.class);
        LOGGER.at(Level.INFO).log("[BootsOfSpeed] System constructed");
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
        if (changed == null) {
            LOGGER.at(Level.INFO).log("[BootsOfSpeed] handle: changed container is null");
            return;
        }

        short capacity = changed.getCapacity();
        LOGGER.at(Level.INFO).log(
            "[BootsOfSpeed] handle: container capacity=" + capacity
            + " (DEFAULT_ARMOR=" + Inventory.DEFAULT_ARMOR_CAPACITY + ")");

        if (capacity != Inventory.DEFAULT_ARMOR_CAPACITY) {
            return;
        }

        Ref<EntityStore> entityRef = chunk.getReferenceTo(index);
        Player player = commandBuffer.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            LOGGER.at(Level.INFO).log("[BootsOfSpeed] handle: not a player entity, skipping");
            return;
        }

        boolean bootsInArmor = false;
        for (short i = 0; i < capacity; i++) {
            ItemStack stack = changed.getItemStack(i);
            String itemId = (stack != null) ? stack.getItemId() : "<null>";
            LOGGER.at(Level.INFO).log("[BootsOfSpeed] armor slot " + i + ": " + itemId);
            if (stack != null && BOOTS_ID.equalsIgnoreCase(stack.getItemId())) {
                bootsInArmor = true;
            }
        }

        EffectControllerComponent ec = commandBuffer.ensureAndGetComponent(
            entityRef, EffectControllerComponent.getComponentType());
        boolean hasEffect = ec.hasEffect(EFFECT_SLOT);
        LOGGER.at(Level.INFO).log(
            "[BootsOfSpeed] bootsInArmor=" + bootsInArmor + " hasEffect=" + hasEffect);

        if (bootsInArmor && !hasEffect) {
            int effectIndex = EntityEffect.getAssetMap().getIndex(EFFECT_ID);
            EntityEffect effect = EntityEffect.getAssetMap().getAsset(effectIndex);
            LOGGER.at(Level.INFO).log(
                "[BootsOfSpeed] effect lookup '" + EFFECT_ID + "' → index=" + effectIndex
                + ", asset=" + (effect != null ? "found" : "NULL"));
            if (effect != null) {
                boolean added = ec.addInfiniteEffect(entityRef, EFFECT_SLOT, effect, commandBuffer);
                LOGGER.at(Level.INFO).log("[BootsOfSpeed] addInfiniteEffect returned: " + added);
            }
        } else if (!bootsInArmor && hasEffect) {
            ec.removeEffect(entityRef, EFFECT_SLOT, commandBuffer);
            LOGGER.at(Level.INFO).log("[BootsOfSpeed] removeEffect called");
        }
    }
}
