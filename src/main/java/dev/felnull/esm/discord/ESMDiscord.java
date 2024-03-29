package dev.felnull.esm.discord;

import dev.felnull.esm.ServerConfig;
import dev.felnull.fnjl.util.DiscordWebHookBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.minecraft.entity.player.EntityPlayer;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class ESMDiscord {
    private static boolean inited;
    private static JDA jda;
    private static boolean stop;

    private static void sendWebHookAsync(String name, String avatar, String contend) {
        if (ServerConfig.getWebhookUrl().isEmpty()) return;
        DiscordWebHookBuilder db = new DiscordWebHookBuilder(ServerConfig.getWebhookUrl(), contend);
        db.setUsername(name);
        db.setAvatarUrl(avatar);
        try {
            db.sendAsync(null);
        } catch (IOException ignored) {
        }
    }

    public static void init() {
        if (inited) return;
        inited = true;

        try {
            jda = JDABuilder.createDefault(ServerConfig.getDiscordToken()).build();
            jda.addEventListener(new DiscordListener());
            setStatus(OnlineStatus.IDLE, Activity.watching("サーバー起動開始"));
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void setStatus(OnlineStatus status, Activity activity) {
        if (jda != null && !stop)
            jda.getPresence().setPresence(status, activity);
    }

    public static void shutdown() {
        if (jda != null)
            jda.shutdownNow();

        stop = true;
    }

    public static void setChannelMessage(String text) {
        text = formattedtMsg(text);
        if (jda != null && !stop)
            getChannel().getManager().setTopic(text).queue();
    }

    public static void sendMessage(String message) {
        message = formattedtMsg(message);
        if (getChannel() == null) return;
        getChannel().sendMessage(new MessageBuilder().append(message).build()).queue();
    }

    public static void sendMessageByPlayerAsync(EntityPlayer player, String message) {
        message = formattedtMsg(message);
        String avtURL = "https://crafatar.com/avatars/%s.png?size=128&overlay#";
        sendWebHookAsync(player.getGameProfile().getName(), String.format(avtURL, player.getGameProfile().getId()), message);
    }

    public static TextChannel getChannel() {
        return jda.getTextChannelById(ServerConfig.getChannelId());
    }

    private static String formattedtMsg(String str) {
        str = str.replace("§r", "");
        return str;
    }
}
