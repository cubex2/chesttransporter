package cubex2.mods.chesttransporter;

import com.google.common.collect.Maps;
import cubex2.mods.chesttransporter.chests.BasicDrawer;
import cubex2.mods.chesttransporter.chests.ChestRegistry;
import cubex2.mods.chesttransporter.chests.CompDrawer;
import cubex2.mods.chesttransporter.chests.CompactChest;
import cubex2.mods.chesttransporter.chests.QuarkChest;
import cubex2.mods.chesttransporter.chests.Spawner;
import cubex2.mods.chesttransporter.chests.TransportableChest;
import java.util.EnumMap;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
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

@Mod(modid = "chesttransporter", name = "Chest Transporter", version = "2.6.0")
public class ChestTransporter
{
    @SidedProxy(clientSide = "cubex2.mods.chesttransporter.ClientProxy", serverSide = "cubex2.mods.chesttransporter.CommonProxy")
    public static CommonProxy proxy;

    private static boolean pickupSpawners = true;

    public static final EnumMap<TransporterType, ItemChestTransporter> items = Maps.newEnumMap(TransporterType.class);
    public static final EnumMap<TransporterType, Boolean> canUseSpawner = Maps.newEnumMap(TransporterType.class);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit();

        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        try
        {
            config.load();
            pickupSpawners = config.getBoolean("pickupSpawners", Configuration.CATEGORY_GENERAL, true, "Set this to false to prevent picking up of mob spawners");

            for (TransporterType type : TransporterType.values())
            {
                canUseSpawner.put(type, config.getBoolean(type.spawnerConfigName(), Configuration.CATEGORY_GENERAL, true, "Set this to false to prevent the " + type.name().toLowerCase() + " transporter to pick up mob spawners"));
            }
        }
        finally
        {
            config.save();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        for (TransporterType type : TransporterType.values())
        {
            ItemChestTransporter item = new ItemChestTransporter(type);
            items.put(type, item);
            item.setRegistryName("chesttransporter", "chesttransporter" + type.nameSuffix);
            GameRegistry.register(item);

            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "S S", "SSS", " M ", 'S', "stickWood", 'M', type.recipeMaterial));
        }

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
            Block block = Block.getBlockFromName("ironchest:iron_chest");
            if (block != null && block != Blocks.AIR)
            {
                String[] names = new String[] {"iron", "gold", "diamond", "copper", "tin", "crystal", "obsidian"};
                for (int i = 0; i < 7; i++)
                {
                    ChestRegistry.register(new TransportableChest(block, i, 3 + i, names[i]));
                }
            }
        }

        if (Loader.isModLoaded("multipagechest"))
        {
            Block block = Block.getBlockFromName("multipagechest:multipagechest");
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

        if (Loader.isModLoaded("storagedrawers"))
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

        if (Loader.isModLoaded("quark"))
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

        if (Loader.isModLoaded("fluidity"))
        {
            Block block = Block.getBlockFromName("fluidity:fluidity_iron_chest");
            if (block != null && block != Blocks.AIR)
            {
                String[] names = new String[] {"bronze", "invar", "electrum", "tin", "brass", "lead",
                        "steel", "nickel", "cold_iron", "adamantine", "aquarium", "mithril", "star_steel",
                        "cupronickel", "platinum"};

                for (int i = 0; i < names.length; i++)
                {
                    ChestRegistry.register(new TransportableChest(block, i, 28 + i, "fluidity_" + names[i]));
                }
            }
        }

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
                    ChestRegistry.registerMinecart((Class<? extends EntityMinecartChest>)Class.forName(classNames[i]), ChestRegistry.dvToChest.get(3 + i));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


}
