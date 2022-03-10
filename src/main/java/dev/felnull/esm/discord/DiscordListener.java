package dev.felnull.esm.discord;

import dev.felnull.esm.ESMUtil;
import dev.felnull.esm.ServerConfig;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

public class DiscordListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if (e.getChannel().getIdLong() != ServerConfig.getChannelId() || e.getAuthor().isBot() || e.getAuthor().isSystem())
            return;
        String message = e.getMessage().getContentDisplay();
        ITextComponent com = (ITextComponent) new TextComponentString("[Discord] ").setStyle(new Style().setColor(TextFormatting.DARK_AQUA));
        ITextComponent n1com = new TextComponentString("<").setStyle(new Style().setColor(TextFormatting.WHITE));
        ITextComponent nkcom = new TextComponentString(e.getAuthor().getName());

        if (e.getMember() != null) {
            if (e.getMember().getNickname() != null) {
                nkcom = new TextComponentString(e.getMember().getNickname()).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(e.getAuthor().getName()).setStyle(new Style().setColor(TextFormatting.DARK_AQUA)))));
            }
            //   if (e.getMember().getColor() != null)
            //       nkcom.setStyle(nkcom.getStyle().setColor(Color.fromRgb(e.getMember().getColor().getRGB())));
        }


        ITextComponent n2com = new TextComponentString("> ").setStyle(new Style().setColor(TextFormatting.WHITE));
        ITextComponent mcom = new TextComponentString(message).setStyle(new Style().setColor(TextFormatting.WHITE));
        sendServerMessage(com.appendSibling(n1com).appendSibling(nkcom).appendSibling(n2com).appendSibling(mcom));
    }


    private static void sendServerMessage(ITextComponent text) {
        ESMUtil.getServer().addScheduledTask(() -> ESMUtil.sendMessageAllPlayer(text));
    }
}