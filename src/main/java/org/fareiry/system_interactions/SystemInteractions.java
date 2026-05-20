package org.fareiry.system_interactions;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.fareiry.system_interactions.command.SystemInteractionsCommands;
import org.fareiry.system_interactions.network.SystemInteractionsNetwork;

@Mod(SystemInteractions.MODID)
public class SystemInteractions {
    public static final String MODID = "system_interactions";

    public SystemInteractions(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modEventBus.addListener(SystemInteractionsNetwork::registerPayloads);
        NeoForge.EVENT_BUS.register(SystemInteractionsCommands.class);
    }
}
