package dev.felnull.esm.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.felnull.esm.ESMUtil;
import dev.felnull.esm.EnumServerMOD;
import dev.felnull.esm.VoteService;
import dev.felnull.esm.music.MusicManager;
import dev.felnull.fnnbs.NBS;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class ServerHandler {
    private static final Gson GSON = new Gson();
    private static final Map<String, Integer> VOTES = new HashMap<>();
    private static final Map<String, Integer> VOTES_COUNT = new HashMap<>();
    private static final Random random = new Random();
    private static long lastPr;

   /* @SubscribeEvent
    public static void onVote(VotifierEvent e) {
        Vote v = e.getVote();
        addVoteCount(v.getUsername());
        ITextComponent svm = VoteService.getByServiceName(v.getServiceName()).getComponent(v.getServiceName());
        ITextComponent msg = new TextComponentString(v.getUsername() + "さんが").appendSibling(svm).appendText("で投票しました!").setStyle(new Style().setColor(TextFormatting.YELLOW));
        ESMUtil.sendMessageAllPlayer(msg);

        if (!VOTES.containsKey(e.getVote().getUsername())) {
            VOTES.put(e.getVote().getUsername(), 1);
        } else {
            VOTES.put(e.getVote().getUsername(), VOTES.get(e.getVote().getUsername()) + 1);
        }

    }*/

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.player.world.isRemote)
            return;
       // e.player.sendStatusMessage(VoteService.getPromotion(null), false);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.player.world.isRemote)
            return;
        String name = e.player.getGameProfile().getName();
        if (VOTES.containsKey(name)) {
            int cont = VOTES.get(name);
            if (cont <= 0)
                return;

            int added = 0;
            for (int i = 0; i < cont; i++) {
                if (random.nextInt(19) == 0) {
                    added++;
                }
            }
            NBS nbs = EnumServerMOD.hqmCompNBS;
            if (added >= 1) {
                e.player.sendStatusMessage(new TextComponentString("投票ありがとナス！").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)), false);
                nbs = EnumServerMOD.worldOfTono;
            } else {
                e.player.sendStatusMessage(new TextComponentString("投票ありがとうございます！").setStyle(new Style().setColor(TextFormatting.GREEN)), false);
            }

            // e.player.sendStatusMessage(new TextComponentString("FTBチームに入る際にインベントリ内のアイテムが消えてしまうようです、消えてほしくない場合は投票アイテムなどを地面に投げてからチームに入ってください。").withStyle(TextFormatting.RED), false);

            MusicManager.getInstance().start(nbs, e.player::getPositionVector, () -> e.player.dimension, UUID.randomUUID(), false, 0);

            for (int i = 0; i < cont + added; i++) {
                ESMUtil.giveItem(e.player, ESMUtil.createVoteItem());
            }

            for (int i = 0; i < 30; i++) {
                Vec3d vec3d = new Vec3d(((double) random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                Vec3d pls = new Vec3d(e.player.posX + ((double) random.nextFloat() - 0.5D), e.player.posY + ((double) random.nextFloat() - 0.5D), e.player.posZ + ((double) random.nextFloat() - 0.5D));
                e.player.world.spawnParticle(EnumParticleTypes.TOTEM, pls.x, pls.y, pls.z, vec3d.x, vec3d.y + 0.05D, vec3d.z);

            }
            VOTES.remove(name);
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent e) {
        if (e.side.isServer() && e.phase == TickEvent.Phase.START) {
            MusicManager.getInstance().tick();
        }
    }


    public static void onServerStart(FMLServerStartingEvent e) {
        File vfile = new File(EnumServerMOD.MODID, "votifier.json");
        if (vfile.exists()) {
            JsonObject jo = null;
            try {
                jo = GSON.fromJson(new BufferedReader(new FileReader(vfile)), JsonObject.class);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
                VOTES.put(entry.getKey(), entry.getValue().getAsInt());
            }
        }

        File vcfile = new File(EnumServerMOD.MODID, "votifier_count.json");
        if (vcfile.exists()) {
            JsonObject jo = null;
            try {
                jo = GSON.fromJson(new BufferedReader(new FileReader(vcfile)), JsonObject.class);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
                VOTES_COUNT.put(entry.getKey(), entry.getValue().getAsInt());
            }
        }
    }

    private static void addVoteCount(String userName) {
        Integer ct = VOTES_COUNT.get(userName);
        if (ct == null)
            ct = 0;
        ct++;
        VOTES_COUNT.put(userName, ct);
    }

    public static void onServerStop(FMLServerStoppingEvent e) {
        File vfile = new File(EnumServerMOD.MODID, "votifier.json");
        vfile.getParentFile().mkdirs();
        {
            JsonObject jo = new JsonObject();
            for (Map.Entry<String, Integer> stringIntegerEntry : VOTES.entrySet()) {
                jo.addProperty(stringIntegerEntry.getKey(), stringIntegerEntry.getValue());
            }
            try {
                Files.write(vfile.toPath(), GSON.toJson(jo).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        File vcfile = new File(EnumServerMOD.MODID, "votifier_count.json");
        vcfile.getParentFile().mkdirs();
        {
            JsonObject jo = new JsonObject();
            for (Map.Entry<String, Integer> stringIntegerEntry : VOTES_COUNT.entrySet()) {
                jo.addProperty(stringIntegerEntry.getKey(), stringIntegerEntry.getValue());
            }
            try {
                Files.write(vcfile.toPath(), GSON.toJson(jo).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


   /* @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.START) return;
        if (System.currentTimeMillis() - lastPr >= 1000 * 60 * 60) {
            lastPr = System.currentTimeMillis();
            ITextComponent pr = VoteService.getPromotion(null);
            ESMUtil.sendMessageAllPlayer(pr);
        }
    }*/
}
