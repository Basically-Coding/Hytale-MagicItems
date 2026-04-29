package dev.basicallycoding.magicitems;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.basicallycoding.magicitems.commands.ExampleCommand;
import dev.basicallycoding.magicitems.events.ExampleEvent;

import javax.annotation.Nonnull;

public class MagicItemsPlugin extends JavaPlugin {

    public MagicItemsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(new ExampleCommand("example", "An example command"));
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, ExampleEvent::onPlayerReady);
    }
}
