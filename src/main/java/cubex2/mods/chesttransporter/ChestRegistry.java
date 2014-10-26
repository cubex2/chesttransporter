package cubex2.mods.chesttransporter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChestRegistry
{
    public static List<TransportableChest> chests = Lists.newArrayList();
    public static Map<Integer, TransportableChest> dvToChest = Maps.newHashMap();
    public static Map<Block, List<TransportableChest>> blockToChests = Maps.newHashMap();

    public static void register(TransportableChest chest)
    {
        chests.add(chest);
        dvToChest.put(chest.getTransporterDV(), chest);

        if (!blockToChests.containsKey(chest.chestBlock))
            blockToChests.put(chest.chestBlock, new ArrayList<TransportableChest>());
        blockToChests.get(chest.chestBlock).add(chest);
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
}
