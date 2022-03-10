package dev.felnull.esm;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.function.Supplier;

public enum VoteService {
    NONE("不明", "unknown", () -> null, TextFormatting.BLACK),
    TESTER("Votifier Tester", "minestatus.net test vote", () -> "https://minestatus.net/tools/votifier", TextFormatting.BLUE),
    JMS("JMS", "minecraft.jp", ServerConfig::getJmsUrl, TextFormatting.AQUA),
    MONOCRAFT("ものくらふと", "monocraft.net", ServerConfig::getMonocraftUrl, TextFormatting.DARK_GREEN);
    private final String name;
    private final String serviceName;
    private final Supplier<String> url;
    private final TextFormatting color;

    private VoteService(String name, String serviceName, Supplier<String> url, TextFormatting color) {
        this.name = name;
        this.serviceName = serviceName;
        this.url = url;
        this.color = color;
    }

    public TextFormatting getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUrl() {
        return url.get();
    }

    public static VoteService getByServiceName(String name) {
        for (VoteService service : values()) {
            if (service.getServiceName().equals(name)) {
                return service;
            }
        }
        return NONE;
    }

    public ITextComponent getComponent(String serviceName) {
        if (serviceName == null)
            serviceName = getServiceName();
        ITextComponent svm = new TextComponentString(getName()).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(serviceName).setStyle(new Style().setColor(TextFormatting.BLUE)))).setColor(getColor()));
        if (getUrl() != null && !getUrl().isEmpty())
            svm = svm.setStyle(svm.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, getUrl())));
        return svm;
    }

    public static ITextComponent getPromotion(VoteService last) {
        ItemStack stack = ESMUtil.createVoteItem();
        ITextComponent vi = new TextComponentString(stack.getDisplayName());

        if (last == null)
            return new TextComponentString("").appendSibling(JMS.getComponent(null)).appendText("または").appendSibling(MONOCRAFT.getComponent(null)).appendText("で投票して").appendSibling(vi).appendText("を手に入れよう!").setStyle(new Style().setColor(TextFormatting.DARK_AQUA));

        VoteService nl = last == JMS ? MONOCRAFT : JMS;
        return new TextComponentString("").appendSibling(nl.getComponent(null)).appendText("でも投票可能です").setStyle(new Style().setColor(TextFormatting.DARK_AQUA));
    }
}