package dev.felnull.esm;

import dev.felnull.fnjl.util.FNDataUtil;
import dev.felnull.fnnbs.NBS;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
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
        MinecraftForge.EVENT_BUS.register(ServerHandler.class);
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
    }

    @Mod.EventHandler
    public void onServetStop(FMLServerStoppingEvent e) {
        ServerHandler.onServerStop(e);
    }

    public static Path getModFolder() {
        return Paths.get("./" + MODID);
    }
}
