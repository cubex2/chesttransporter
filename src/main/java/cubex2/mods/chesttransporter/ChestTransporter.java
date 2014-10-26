package cubex2.mods.chesttransporter;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = "ChestTransporter", name = "Chest Transporter", version = "1.2.0")
public class ChestTransporter
{
    @Instance("ChestTransporter")
    public static ChestTransporter instance;
    public static ItemChestTransporter chestTransporter;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        chestTransporter = new ItemChestTransporter();
        GameRegistry.registerItem(chestTransporter, "chesttransporter");
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chestTransporter), "S S", "SSS", " S ", 'S', Items.stick));
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt)
    {
        ChestRegistry.register(new TransportableChest(Blocks.chest, -1, 1));
        ChestRegistry.register(new TransportableChest(Blocks.trapped_chest, -1, 10));

        if (Loader.isModLoaded("IronChest"))
        {
            Block block = GameData.getBlockRegistry().getObject("IronChest:BlockIronChest");
            if (block != null && block != Blocks.air)
            {
                for (int i = 0; i < 6; i++)
                {
                    ChestRegistry.register(new TransportableChest(block, i, 2 + i));
                }
                ChestRegistry.register(new TransportableChest(block, 6, 9));
            }
        }

        if (Loader.isModLoaded("MultiPageChest"))
        {
            Block block = GameData.getBlockRegistry().getObject("MultiPageChest:multipagechest");
            if (block != null && block != Blocks.air)
            {
                ChestRegistry.register(new TransportableChest(block, -1, 8));
            }
        }

        if (Loader.isModLoaded("factorization"))
        {
            Block block = GameData.getBlockRegistry().getObject("factorization:FzBlock");
            if (block != null && block != Blocks.air)
            {
                ChestRegistry.register(new FzBarrel(block, 2, 11));
            }
        }
    }

}
