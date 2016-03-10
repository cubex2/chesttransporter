package cubex2.mods.chesttransporter;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = "ChestTransporter", name = "Chest Transporter", version = "2.2.1")
public class ChestTransporter
{
    @Mod.Instance("ChestTransporter")
    public static ChestTransporter instance;

    @SidedProxy(clientSide = "cubex2.mods.chesttransporter.ClientProxy", serverSide = "cubex2.mods.chesttransporter.CommonProxy")
    public static CommonProxy proxy;

    public static ItemChestTransporter chestTransporter;
    public static ItemChestTransporter chestTransporterIron;
    public static ItemChestTransporter chestTransporterGold;
    public static ItemChestTransporter chestTransporterDiamond;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        chestTransporter = new ItemChestTransporter(1, "wood");
        chestTransporterIron = new ItemChestTransporter(9, "iron");
        chestTransporterGold = new ItemChestTransporter(19, "gold");
        chestTransporterDiamond = new ItemChestTransporter(79, "diamond");

        GameRegistry.registerItem(chestTransporter, "chesttransporter");
        GameRegistry.registerItem(chestTransporterIron, "chesttransporter_iron");
        GameRegistry.registerItem(chestTransporterGold, "chesttransporter_gold");
        GameRegistry.registerItem(chestTransporterDiamond, "chesttransporter_diamond");

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chestTransporter), "S S", "SSS", " S ", 'S', Items.stick));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chestTransporterIron), "S S", "SSS", " M ", 'S', Items.stick, 'M', Items.iron_ingot));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chestTransporterGold), "S S", "SSS", " M ", 'S', Items.stick, 'M', Items.gold_ingot));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chestTransporterDiamond), "S S", "SSS", " M ", 'S', Items.stick, 'M', Items.diamond));

        proxy.registerModels();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt)
    {
        TransportableChest chest = new TransportableChest(Blocks.chest, -1, 1, "vanilla");
        ChestRegistry.register(chest);
        ChestRegistry.registerMinecart(EntityMinecartChest.class, chest);

        ChestRegistry.register(new TransportableChest(Blocks.trapped_chest, -1, 2, "vanilla_trapped"));

        if (Loader.isModLoaded("IronChest"))
        {
            Block block = GameData.getBlockRegistry().getObject(new ResourceLocation("IronChest:BlockIronChest"));
            if (block != null && block != Blocks.air)
            {
                String[] names = new String[]{"iron", "gold", "diamond", "copper", "tin", "crystal", "obsidian"};
                for (int i = 0; i < 7; i++)
                {
                    ChestRegistry.register(new TransportableChest(block, i, 3 + i, names[i]));
                }
            }
        }

        if (Loader.isModLoaded("MultiPageChest"))
        {
            Block block = GameData.getBlockRegistry().getObject(new ResourceLocation("MultiPageChest:multipagechest"));
            if (block != null && block != Blocks.air)
            {
                ChestRegistry.register(new TransportableChest(block, -1, 10, "multipagechest"));
            }
        }

        /*if (Loader.isModLoaded("factorization"))
        {
            Block block = GameData.getBlockRegistry().getObject("factorization:FzBlock");
            if (block != null && block != Blocks.air)
            {
                ChestRegistry.register(new FzBarrel(block, 2, 11));
            }
        }

        if (Loader.isModLoaded("varietychests"))
        {
            Block block = GameData.getBlockRegistry().getObject("varietychests:customchest");
            if (block != null && block != Blocks.air)
            {
                ChestRegistry.register(new VariertyChest(block, -1, 12, false));
            }
            block = GameData.getBlockRegistry().getObject("varietychests:customglowingchest");
            if (block != null && block != Blocks.air)
            {
                ChestRegistry.register(new VariertyChest(block, -1, 13, true));
            }
        }*/

        if (Loader.isModLoaded("compactchests"))
        {
            String[] names = new String[]{"quadruple", "sextuple", "triple", "double", "quintuple"};
            for (int i = 0; i < names.length; i++)
            {
                Block block = GameData.getBlockRegistry().getObject(new ResourceLocation("compactchests:" + names[i] + "Chest"));
                if (block != null && block != Blocks.air)
                {
                    ChestRegistry.register(new TransportableChest(block, -1, 14 + i, "cc_" + names[i]));
                }
            }
        } else if (Loader.isModLoaded("compactstorage"))
        {
            String[] names = new String[]{"quadruple", "sextuple", "triple", "double", "quintuple"};
            for (int i = 0; i < names.length; i++)
            {
                Block block = GameData.getBlockRegistry().getObject(new ResourceLocation("compactstorage:" + names[i] + "Chest"));
                if (block != null && block != Blocks.air)
                {
                    ChestRegistry.register(new TransportableChest(block, -1, 14 + i, "cc_" + names[i]));
                }
            }
        }

        if (Loader.isModLoaded("StorageDrawers"))
        {
            Block block = GameData.getBlockRegistry().getObject(new ResourceLocation("storagedrawers:basicDrawers"));
            if (block != null && block != Blocks.air)
            {
                String[] names = new String[]{"full1", "full2", "full4", "half2", "half4"};
                for (int i = 0; i < names.length; i++)
                {
                    ChestRegistry.register(new BasicDrawer(block, i, 19 + i, "basic_drawer_" + names[i]));
                }
            }

            block = GameData.getBlockRegistry().getObject(new ResourceLocation("storagedrawers:compDrawers"));
            if (block != null && block != Blocks.air)
            {
                ChestRegistry.register(new CompDrawer(block, 0, 23, "comp_drawer"));
            }
        }

        if (Loader.isModLoaded("ironchestminecarts") && Loader.isModLoaded("IronChest"))
        {
            String[] classNames = new String[]{
                    "ganymedes01.ironchestminecarts.minecarts.types.EntityMinecartIronChest",
                    "ganymedes01.ironchestminecarts.minecarts.types.EntityMinecartGoldChest",
                    "ganymedes01.ironchestminecarts.minecarts.types.EntityMinecartDiamondChest",
                    "ganymedes01.ironchestminecarts.minecarts.types.EntityMinecartCopperChest",
                    "ganymedes01.ironchestminecarts.minecarts.types.EntityMinecartSilverChest",
                    "ganymedes01.ironchestminecarts.minecarts.types.EntityMinecartCrystalChest",
                    "ganymedes01.ironchestminecarts.minecarts.types.EntityMinecartObsidianChest"};

            try
            {
                for (int i = 0; i < 7; i++)
                {
                    ChestRegistry.registerMinecart((Class<? extends EntityMinecartChest>) Class.forName(classNames[i]), ChestRegistry.dvToChest.get(3 + i));
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if (Loader.isModLoaded("extracarts") && Loader.isModLoaded("IronChest"))
        {
            String[] classNames = new String[]{
                    "com.dta.extracarts.mods.ironchest.entities.EntityIronChestCart",
                    "com.dta.extracarts.mods.ironchest.entities.EntityGoldChestCart",
                    "com.dta.extracarts.mods.ironchest.entities.EntityDiamondChestCart",
                    "com.dta.extracarts.mods.ironchest.entities.EntityCopperChestCart",
                    "com.dta.extracarts.mods.ironchest.entities.EntitySilverChestCart",
                    "com.dta.extracarts.mods.ironchest.entities.EntityCrystalChestCart",
                    "com.dta.extracarts.mods.ironchest.entities.EntityObsidianChestCart"
            };

            try
            {
                for (int i = 0; i < 7; i++)
                {
                    ChestRegistry.registerMinecart((Class<? extends EntityMinecartChest>) Class.forName(classNames[i]), ChestRegistry.dvToChest.get(3 + i));
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


}
