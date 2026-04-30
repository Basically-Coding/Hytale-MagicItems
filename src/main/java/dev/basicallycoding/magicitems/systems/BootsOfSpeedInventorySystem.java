package dev.basicallycoding.magicitems.systems;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.InventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.basicallycoding.magicitems.config.MagicItemsConfig;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class BootsOfSpeedInventorySystem extends EntityEventSystem<EntityStore, InventoryChangeEvent> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String BOOTS_ID = "boots_of_speed";

    private final MagicItemsConfig config;

    public BootsOfSpeedInventorySystem(@Nonnull MagicItemsConfig config) {
        super(InventoryChangeEvent.class);
        this.config = config;
        LOGGER.at(Level.INFO).log(
            "[BootsOfSpeed] System constructed (speedMultiplier=" + config.speedMultiplier() + ")");
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

        MovementManager mm = commandBuffer.getComponent(entityRef, MovementManager.getComponentType());
        if (mm == null) {
            LOGGER.at(Level.WARNING).log("[BootsOfSpeed] MovementManager null on player; cannot apply buff");
            return;
        }

        // Idempotent: reset to defaults, then re-apply buff if boots present.
        // Avoids compounding multipliers across repeated equip-state events.
        mm.applyDefaultSettings();
        if (bootsInArmor) {
            float multiplier = config.speedMultiplier();
            MovementSettings s = mm.getSettings();
            s.forwardRunSpeedMultiplier    *= multiplier;
            s.forwardSprintSpeedMultiplier *= multiplier;
            s.backwardRunSpeedMultiplier   *= multiplier;
            s.strafeRunSpeedMultiplier     *= multiplier;
        }

        PlayerRef pr = player.getPlayerRef();
        mm.update(pr.getPacketHandler());

        LOGGER.at(Level.INFO).log(
            "[BootsOfSpeed] bootsInArmor=" + bootsInArmor
            + " → multiplier=" + (bootsInArmor ? config.speedMultiplier() : 1.0f));
    }
}
