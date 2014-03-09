package cubex2.mods.chesttransporter;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = "ChestTransporter", name = "Chest Transporter", version = "1.1.8")
public class ChestTransporter
{
    @Instance("ChestTransporter")
    public static ChestTransporter instance;
    public static ItemChestTransporter chestTransporter;

    public static Block ironChestBlock;
    public static Block multiPageChestBlock;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        chestTransporter = new ItemChestTransporter();
        GameRegistry.registerItem(chestTransporter, "chesttransporter");
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chestTransporter), "S S", "SSS", " S ", 'S', Items.stick));
    }

    @EventHandler
    public void load(FMLInitializationEvent evt)
    {

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt)
    {
        try
        {
            Class clazz = Class.forName("cpw.mods.ironchest.IronChest");
            ironChestBlock = (Block) clazz.getField("ironChestBlock").get(null);
        } catch (ClassNotFoundException e)
        {
            // IronChest is not installed
        } catch (Exception e)
        {
        }
        try
        {
            Class clazz = Class.forName("cubex2.mods.multipagechest.MultiPageChest");
            multiPageChestBlock = (Block) clazz.getField("chestBlock").get(null);
        } catch (ClassNotFoundException e)
        {
            // MultiPageChest is not installed
        } catch (Exception e)
        {
        }
    }

}
