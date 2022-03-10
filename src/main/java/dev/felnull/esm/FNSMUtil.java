package dev.felnull.esm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.function.Consumer;

public class FNSMUtil {
    public static MinecraftServer getServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public static void executionAllPlayer(Consumer<EntityPlayerMP> consumer) {
        if (getServer() != null)
            getServer().getPlayerList().getPlayers().forEach(consumer);
    }

    public static void sendMessageAllPlayer(ITextComponent component) {
        executionAllPlayer(n -> n.sendStatusMessage(component, false));
    }

    public static ItemStack createVoteItem() {
        Item item = Items.PAPER;// ForgeRegistries.ITEMS.getValue(new ResourceLocation("appliedenergistics2:singularity"));
        //  if (item == null || item == Items.AIR)
        //     item = Items.DIAMOND;
        ItemStack stack = new ItemStack(item);

        stack.setStackDisplayName("§9投票紙");
       /* Map<Enchantment, Integer> encs = new HashMap<>();
        encs.put(Enchantments.RIPTIDE, 0);
        EnchantmentHelper.setEnchantments(encs, stack);
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putBoolean("Unbreakable", true);
        tag.putInt("HideFlags", 5);

        CompoundNBT display = tag.getCompound("display");
        ListNBT lore = display.getList("Lore", 8);

        IFormattableTextComponent com = new StringTextComponent("お、先輩こいつ玉とか言いだしましたよ？").withStyle(Style.EMPTY.withColor(Color.fromRgb(0x364364)));
        IFormattableTextComponent com2 = new StringTextComponent("やっぱ好きなんですねぇ").withStyle(Style.EMPTY.withColor(Color.fromRgb(0x364364)));
        lore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(com)));
        lore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(com2)));
        display.put("Lore", lore);
        tag.put("display", display);*/
        return stack;
    }

    public static void giveItem(EntityPlayer player, ItemStack stack) {
        if (!player.addItemStackToInventory(stack))
            player.dropItem(stack, false, true);
    }

}
