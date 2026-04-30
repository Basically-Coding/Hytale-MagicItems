package dev.basicallycoding.magicitems;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.basicallycoding.magicitems.commands.ExampleCommand;
import dev.basicallycoding.magicitems.config.ConfigLoader;
import dev.basicallycoding.magicitems.config.MagicItemsConfig;
import dev.basicallycoding.magicitems.events.ExampleEvent;
import dev.basicallycoding.magicitems.systems.BootsOfSpeedInventorySystem;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public class MagicItemsPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public MagicItemsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        Path configPath = this.getDataDirectory().resolve("config.json");
        MagicItemsConfig config = ConfigLoader.loadOrCreate(configPath, LOGGER);

        this.getCommandRegistry().registerCommand(new ExampleCommand("example", "An example command"));
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, ExampleEvent::onPlayerReady);
        this.getEntityStoreRegistry().registerSystem(new BootsOfSpeedInventorySystem(config));
    }
}
