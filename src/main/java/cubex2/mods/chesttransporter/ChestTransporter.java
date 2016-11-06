package cubex2.mods.chesttransporter;

import cubex2.mods.chesttransporter.chests.*;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = "ChestTransporter", name = "Chest Transporter", version = "2.5.4")
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

    private static boolean pickupSpawners = true;
    public static boolean spawnerWithWood = true;
    public static boolean spawnerWithIron = true;
    public static boolean spawnerWithGold = true;
    public static boolean spawnerWithDiamond = true;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit();

        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        try
        {
            config.load();
            pickupSpawners = config.getBoolean("pickupSpawners", Configuration.CATEGORY_GENERAL, true, "Set this to false to prevent picking up of mob spawners");
            spawnerWithWood = config.getBoolean("spawnerWithWood", Configuration.CATEGORY_GENERAL, true, "Set this to false to prevent the wooden transporter to pick up mob spawners");
            spawnerWithIron = config.getBoolean("spawnerWithIron", Configuration.CATEGORY_GENERAL, true, "Set this to false to prevent the iron transporter to pick up mob spawners");
            spawnerWithGold = config.getBoolean("spawnerWithGold", Configuration.CATEGORY_GENERAL, true, "Set this to false to prevent the golden transporter to pick up mob spawners");
            spawnerWithDiamond = config.getBoolean("spawnerWithDiamond", Configuration.CATEGORY_GENERAL, true, "Set this to false to prevent the diamond transporter to pick up mob spawners");
        } finally
        {
            config.save();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        chestTransporter = new ItemChestTransporter(1, "wood");
        chestTransporterIron = new ItemChestTransporter(9, "iron");
        chestTransporterGold = new ItemChestTransporter(19, "gold");
        chestTransporterDiamond = new ItemChestTransporter(79, "diamond");

        chestTransporter.setRegistryName("chesttransporter", "chesttransporter");
        chestTransporterIron.setRegistryName("chesttransporter", "chesttransporter_iron");
        chestTransporterGold.setRegistryName("chesttransporter", "chesttransporter_gold");
        chestTransporterDiamond.setRegistryName("chesttransporter", "chesttransporter_diamond");

        GameRegistry.register(chestTransporter);
        GameRegistry.register(chestTransporterIron);
        GameRegistry.register(chestTransporterGold);
        GameRegistry.register(chestTransporterDiamond);

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chestTransporter), "S S", "SSS", " S ", 'S', Items.STICK));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chestTransporterIron), "S S", "SSS", " M ", 'S', Items.STICK, 'M', Items.IRON_INGOT));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chestTransporterGold), "S S", "SSS", " M ", 'S', Items.STICK, 'M', Items.GOLD_INGOT));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chestTransporterDiamond), "S S", "SSS", " M ", 'S', Items.STICK, 'M', Items.DIAMOND));

        proxy.registerModels();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt)
    {
        TransportableChest chest = new TransportableChest(Blocks.CHEST, -1, 1, "vanilla");
        ChestRegistry.register(chest);
        ChestRegistry.registerMinecart(EntityMinecartChest.class, chest);

        ChestRegistry.register(new TransportableChest(Blocks.TRAPPED_CHEST, -1, 2, "vanilla_trapped"));

        if (Loader.isModLoaded("ironchest"))
        {
            Block block = Block.getBlockFromName("ironchest:BlockIronChest");
            if (block != null && block != Blocks.AIR)
            {
                String[] names = new String[] {"iron", "gold", "diamond", "copper", "tin", "crystal", "obsidian"};
                for (int i = 0; i < 7; i++)
                {
                    ChestRegistry.register(new TransportableChest(block, i, 3 + i, names[i]));
                }
            }
        }

        if (Loader.isModLoaded("MultiPageChest"))
        {
            Block block = Block.getBlockFromName("MultiPageChest:multipagechest");
            if (block != null && block != Blocks.AIR)
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

        if (Loader.isModLoaded("compactstorage"))
        {
            Block block = Block.getBlockFromName("compactstorage:compactChest");
            if (block != null && block != Blocks.AIR)
            {
                ChestRegistry.register(new CompactChest(block, -1, 14, "compact_chest"));
            }
        }

        if (Loader.isModLoaded("StorageDrawers"))
        {
            Block block = Block.getBlockFromName("storagedrawers:basicDrawers");
            if (block != null && block != Blocks.AIR)
            {
                String[] names = new String[] {"full1", "full2", "full4", "half2", "half4"};
                for (int i = 0; i < names.length; i++)
                {
                    int dv = 19 + i;
                    if (dv == 23)
                        dv = 27;
                    ChestRegistry.register(new BasicDrawer(block, i, dv, "basic_drawer_" + names[i]));
                }
            }

            block = Block.getBlockFromName("storagedrawers:compDrawers");
            if (block != null && block != Blocks.AIR)
            {
                ChestRegistry.register(new CompDrawer(block, 0, 23, "comp_drawer"));
            }
        }

        if (pickupSpawners)
        {
            ChestRegistry.register(new Spawner(Blocks.MOB_SPAWNER, -1, 24, "spawner"));
        }

        if (Loader.isModLoaded("Quark"))
        {
            Block block = Block.getBlockFromName("quark:custom_chest");
            if (block != null && block != Blocks.AIR)
            {
                ChestRegistry.register(new QuarkChest(block, 25, "quark_chest"));
            }

            block = Block.getBlockFromName("quark:custom_chest_trap");
            if (block != null && block != Blocks.AIR)
            {
                ChestRegistry.register(new QuarkChest(block, 26, "quark_chest"));
            }
        }

        // 27 is already used

        if (Loader.isModLoaded("ironchestminecarts") && Loader.isModLoaded("ironchest"))
        {
            String[] classNames = new String[] {
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

        if (Loader.isModLoaded("extracarts") && Loader.isModLoaded("ironchest"))
        {
            String[] classNames = new String[] {
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
