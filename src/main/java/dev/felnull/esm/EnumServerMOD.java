package dev.felnull.esm;

import dev.felnull.esm.discord.ESMDiscord;
import dev.felnull.esm.handler.NotificationDiscordHandler;
import dev.felnull.esm.handler.ServerHandler;
import dev.felnull.fnjl.util.FNDataUtil;
import dev.felnull.fnnbs.NBS;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(modid = EnumServerMOD.MODID, name = EnumServerMOD.NAME, version = EnumServerMOD.VERSION, acceptableRemoteVersions = "*")
public class EnumServerMOD {
    public static final String MODID = "enumservermod";
    public static final String NAME = "Enum Server MOD";
    public static final String VERSION = "1.0";
    public static NBS hqmCompNBS;
    public static NBS worldOfTono;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ServerConfig.init();
        ESMDiscord.init();
        ESMDiscord.setStatus(OnlineStatus.IDLE, Activity.watching("サーバー初期読み込み中"));
        MinecraftForge.EVENT_BUS.register(ServerHandler.class);
        MinecraftForge.EVENT_BUS.register(NotificationDiscordHandler.class);
        try {
            hqmCompNBS = new NBS(FNDataUtil.resourceExtractor(EnumServerMOD.class, "nbs/HQMComplete.nbs"));
            worldOfTono = new NBS(FNDataUtil.resourceExtractor(EnumServerMOD.class, "nbs/world_of_tono.nbs"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServetStart(FMLServerStartingEvent e) {
        ServerHandler.onServerStart(e);
        NotificationDiscordHandler.onServerStart(e);
    }

    @Mod.EventHandler
    public void onServetStop(FMLServerStoppingEvent e) {
        ServerHandler.onServerStop(e);
        NotificationDiscordHandler.onServerStop(e);
    }

    @Mod.EventHandler
    public void onServetStop(FMLServerStoppedEvent e) {
        NotificationDiscordHandler.onServerStopped(e);
    }

    public static Path getModFolder() {
        return Paths.get("./" + MODID);
    }
}
