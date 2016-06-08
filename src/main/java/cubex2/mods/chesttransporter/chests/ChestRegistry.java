package cubex2.mods.chesttransporter.chests;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cubex2.mods.chesttransporter.chests.TransportableChest;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChestRegistry
{
    public static List<TransportableChest> chests = Lists.newArrayList();
    public static Map<Integer, TransportableChest> dvToChest = Maps.newHashMap();
    public static Map<Block, List<TransportableChest>> blockToChests = Maps.newHashMap();

    public static Set<Class<? extends EntityMinecart>> minecarts = Sets.newHashSet();
    public static Map<Class<? extends EntityMinecart>, TransportableChest> minecartToChest = Maps.newHashMap();
    public static Map<TransportableChest, Class<? extends EntityMinecart>> chestToMinecart = Maps.newHashMap();

    public static void register(TransportableChest chest)
    {
        chests.add(chest);
        dvToChest.put(chest.getTransporterDV(), chest);

        if (!blockToChests.containsKey(chest.chestBlock))
            blockToChests.put(chest.chestBlock, new ArrayList<TransportableChest>());
        blockToChests.get(chest.chestBlock).add(chest);
    }

    public static void registerMinecart(Class<? extends EntityMinecart> clazz, TransportableChest chest)
    {
        minecarts.add(clazz);
        minecartToChest.put(clazz, chest);
        chestToMinecart.put(chest, clazz);
    }

    public static boolean isChest(Block block, int meta)
    {
        return getChest(block, meta) != null;
    }

    public static TransportableChest getChest(Block block, int meta)
    {
        if (!blockToChests.containsKey(block))
            return null;

        for (TransportableChest chest : blockToChests.get(block))
        {
            if (chest.chestMeta == -1) return chest;
            if (chest.chestMeta == meta) return chest;
        }

        return null;
    }

    public static boolean isSupportedMinecart(EntityMinecart minecart)
    {
        return minecarts.contains(minecart.getClass());
    }

    public static boolean isMinecartChest(int dv)
    {
        return chestToMinecart.containsKey(dvToChest.get(dv));
    }

    public static Class<? extends EntityMinecart> getMinecartClass(int dv)
    {
        return chestToMinecart.get(dvToChest.get(dv));
    }

    public static EntityMinecart createMinecart(World world, int dv)
    {
        try
        {
            return getMinecartClass(dv).getConstructor(World.class).newInstance(world);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static int getChestType(EntityMinecart minecart)
    {
        return minecartToChest.get(minecart.getClass()).transporterDV;
    }
}
