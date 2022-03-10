package dev.felnull.esm.music;

import dev.felnull.esm.FNSMUtil;
import dev.felnull.fnnbs.NBS;
import dev.felnull.fnnbs.instrument.VanillaInstrument;
import dev.felnull.fnnbs.player.AsyncNBSPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.UUID;
import java.util.function.Supplier;

public class MusicPlayer {
    private static int numCt;
    private final UUID uuid;
    private final NBS nbs;
    private final Supplier<Vec3d> playPos;
    private final Supplier<Integer> playDim;
    private final int loopCount;
    private final int num;
    private AsyncNBSPlayer nbsPlayer;

    public MusicPlayer(UUID uuid, NBS nbs, Supplier<Vec3d> playPos, Supplier<Integer> playDim, int loopCount) {
        this.uuid = uuid;
        this.nbs = nbs;
        this.playPos = playPos;
        this.playDim = playDim;
        this.loopCount = loopCount;
        this.num = numCt++;
    }

    public void start(boolean loop) {
        if (nbsPlayer == null) {
            nbsPlayer = new AsyncNBSPlayer(nbs, (iInstrument, volume, pitch, stereo) -> {
                String iname = iInstrument.getSoundName();
                if (iInstrument instanceof VanillaInstrument)
                    iname = iname.replace(".note_block.", ".note.");
                SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(iname));
                ring(event, volume, pitch);
            });
            nbsPlayer.setForcedLoop(loop);
            nbsPlayer.setMaxLoopCount(loopCount);
            nbsPlayer.setPlayThreadName("Music Player " + num);
            nbsPlayer.playStart();
        }
    }

    public void stop() {
        if (nbsPlayer != null) {
            nbsPlayer.playStop();
            nbsPlayer = null;
        }
    }

    public boolean isPlaying() {
        if (nbsPlayer != null)
            return nbsPlayer.isPlaying();
        return false;
    }

    private void ring(SoundEvent soundEvent, float volume, float pitch) {
        if (soundEvent == null) return;
        MinecraftServer server = FNSMUtil.getServer();
        if (server != null) {
            server.addScheduledTask(() -> {
                World world = getWorld(server);
                Vec3d p = playPos.get();
                world.playSound(null, p.x, p.y, p.z, soundEvent, SoundCategory.MASTER, volume, pitch);
            });
        }
    }

    private World getWorld(MinecraftServer server) {
        return server.getWorld(playDim.get());
    }
}
