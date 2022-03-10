package dev.felnull.esm;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ESMUtil {
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

        stack.setStackDisplayName("§9投票特典交換用紙");
        Map<Enchantment, Integer> encs = new HashMap<>();
        encs.put(Enchantments.POWER, 114514);
        EnchantmentHelper.setEnchantments(encs, stack);
        NBTTagCompound tag = stack.getTagCompound();
        tag.setBoolean("Unbreakable", true);
        tag.setInteger("HideFlags", 5);
        return stack;
    }

    public static void giveItem(EntityPlayer player, ItemStack stack) {
        if (!player.addItemStackToInventory(stack))
            player.dropItem(stack, false, true);
    }

    public static double getTPS(World world, int dim) {
        long[] times = world.getMinecraftServer().worldTickTimes.get(dim);
        if (times == null) return -1;
        double worldTickTime = mean(times) * 1.0E-6D;
        return Math.min(1000.0 / worldTickTime, 20);
    }

    private static long mean(long[] values) {
        long sum = 0L;
        for (long v : values)
            sum += v;
        return sum / values.length;
    }
}
