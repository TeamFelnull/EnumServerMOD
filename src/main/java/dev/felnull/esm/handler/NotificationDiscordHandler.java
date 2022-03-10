package dev.felnull.esm.handler;

import com.vexsoftware.votifier.model.Vote;
import dev.felnull.esm.ESMUtil;
import dev.felnull.esm.VoteService;
import dev.felnull.esm.discord.ESMDiscord;
import dev.felnull.katyouvotifier.event.VotifierEvent;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class NotificationDiscordHandler {
    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.0");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日hh時mm分");
    private static long lastUpdate;
    private static long startTime;

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent e) {
        if (System.currentTimeMillis() - lastUpdate >= 1000 * 60 * 3) {
            lastUpdate = System.currentTimeMillis();
            ESMDiscord.setStatus(OnlineStatus.ONLINE, Activity.playing(createActivityMessage(ESMUtil.getServer())));
            //  FNSMDiscord.setChannelMessage(String.format("現在の状態: %s/%s人のオンラインプレイヤー | %sからオンライン", server.getPlayerCount(), server.getMaxPlayers(), timeFormat.format(new Date(startTime))));
        }
    }


    public static void onServerStart(FMLServerStartingEvent e) {
        ESMDiscord.setStatus(OnlineStatus.ONLINE, Activity.playing(getActiviteName()));
        ESMDiscord.sendMessage(":fish: サーバーが開きました！");
        lastUpdate = System.currentTimeMillis() - (1000 * 60 * 2);
        startTime = System.currentTimeMillis();
        ESMDiscord.setStatus(OnlineStatus.ONLINE, Activity.playing(createActivityMessage(e.getServer())));
        //FNSMDiscord.setChannelMessage(String.format("現在の状態: %s/%s人のオンラインプレイヤー | %sからオンライン", server.getPlayerCount(), server.getMaxPlayers(), timeFormat.format(new Date(startTime))));
    }

    private static String createActivityMessage(MinecraftServer server) {
        World ovlv = server.getWorld(0);

        double tpsc = ESMUtil.getTPS(ovlv, 0);
        if (tpsc >= 0)
            return String.format(getActiviteName() + "(%sTPS %s/%s人)", TIME_FORMATTER.format(tpsc), server.getCurrentPlayerCount(), server.getMaxPlayers());
        return String.format(getActiviteName() + "(%s/%s人)", server.getCurrentPlayerCount(), server.getMaxPlayers());
    }

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent e) {
        ESMDiscord.sendMessageByPlayerAsync(e.getPlayer(), e.getMessage());
    }

    private static String getActiviteName() {
        return "2022TeamEnumサーバー";
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent e) {
        if (e.getEntityLiving().world.isRemote) return;
        if (e.getEntityLiving() instanceof EntityPlayer) {
            ITextComponent text = e.getSource().getDeathMessage(e.getEntityLiving());
            ESMDiscord.sendMessageByPlayerAsync((EntityPlayer) e.getEntityLiving(), text.getFormattedText());
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.player.world.isRemote) return;
        ESMDiscord.sendMessageByPlayerAsync(e.player, "サーバーに参加しました");
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        if (e.player.world.isRemote) return;
        ESMDiscord.sendMessageByPlayerAsync(e.player, "サーバーから退出しました");
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent e) {
        if (e.getEntityPlayer().world.isRemote) return;
        if (e.getAdvancement().getDisplay() != null && e.getAdvancement().getDisplay().shouldAnnounceToChat()) {
            ESMDiscord.sendMessageByPlayerAsync(e.getEntityPlayer(), String.format("**%s** を達成した", e.getAdvancement().getDisplay().getTitle().getFormattedText()));
        }
    }

    @SubscribeEvent
    public static void onVote(VotifierEvent e) {
        Vote v = e.getVote();
        EntityPlayerMP player = ESMUtil.getServer().getPlayerList().getPlayerByUsername(v.getUsername());
        VoteService vs = VoteService.getByServiceName(v.getServiceName());
        if (player != null) {
            ESMDiscord.sendMessageByPlayerAsync(player, String.format("**%s** で投票しました", vs.getName()));
        } else {
            ESMDiscord.sendMessage(String.format("%sさんが **%s** で投票しました", v.getUsername(), vs.getName()));
        }
    }

    public static void onServerStop(FMLServerStoppingEvent e) {
        //    FNSMDiscord.setChannelMessage("起動していません");
        ESMDiscord.sendMessage(":fishing_pole_and_fish: サーバーが停止しました！");
        ESMDiscord.setStatus(OnlineStatus.IDLE, Activity.watching("サーバー停止"));
    }

    public static void onServerStopped(FMLServerStoppedEvent e) {
        ESMDiscord.shutdown();
    }
}
