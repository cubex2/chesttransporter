package cubex2.mods.chesttransporter;

import com.google.common.collect.Maps;
import cubex2.mods.chesttransporter.api.TransportableChest;
import cubex2.mods.chesttransporter.chests.*;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.EnumMap;

import static cubex2.mods.chesttransporter.ChestTransporter.ID;

@Mod(modid = ID, name = "Chest Transporter", version = "2.8.7")
public class ChestTransporter
{
    @SidedProxy(clientSide = "cubex2.mods.chesttransporter.ClientProxy", serverSide = "cubex2.mods.chesttransporter.CommonProxy")
    public static CommonProxy proxy;

    private static boolean pickupSpawners = true;
    public static boolean debuffSlowness = true;
    public static boolean debuffMiningFatigue = true;
    public static boolean debuffJump = true;
    public static boolean debuffHunger = true;

    public static final EnumMap<TransporterType, ItemChestTransporter> items = Maps.newEnumMap(TransporterType.class);
    public static final EnumMap<TransporterType, Boolean> canUseSpawner = Maps.newEnumMap(TransporterType.class);
    public static final String ID = "chesttransporter";

    public ChestTransporter()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCreateRegistry(RegistryEvent.NewRegistry event)
    {
        ChestRegistry.init();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit();

        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        try
        {
            config.load();
            pickupSpawners = config.getBoolean("pickupSpawners", Configuration.CATEGORY_GENERAL, true, "Set this to false to prevent picking up of mob spawners");
            debuffSlowness = config.getBoolean("debuffSlowness", Configuration.CATEGORY_GENERAL, true, "Set to false to disable the slowness debuff");
            debuffMiningFatigue = config.getBoolean("debuffMiningFatigue", Configuration.CATEGORY_GENERAL, true, "Set to false to disable the mining fatigue debuff");
            debuffJump = config.getBoolean("debuffJump", Configuration.CATEGORY_GENERAL, true, "Set to false to disable the jump debuff");
            debuffHunger = config.getBoolean("debuffHunger", Configuration.CATEGORY_GENERAL, true, "Set to false to disable the hunger debuff");

            for (TransporterType type : TransporterType.values())
            {
                canUseSpawner.put(type, config.getBoolean(type.spawnerConfigName(), Configuration.CATEGORY_GENERAL, true, "Set this to false to prevent the " + type.name().toLowerCase() + " transporter to pick up mob spawners"));
            }
        } finally
        {
            config.save();
        }

        for (TransporterType type : TransporterType.values())
        {
            ItemChestTransporter item = new ItemChestTransporter(type);
            items.put(type, item);
            item.setRegistryName("chesttransporter", "chesttransporter" + type.nameSuffix);
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        items.values().forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event)
    {
        proxy.registerModels();
    }

    @SubscribeEvent
    public void registerChests(RegistryEvent.Register<TransportableChest> event)
    {
        IForgeRegistry<TransportableChest> registry = event.getRegistry();

        TransportableChestImpl chest = new TransportableChestOld(Blocks.CHEST, -1, 1, "vanilla");
        registry.register(chest);
        ChestRegistry.registerMinecart(EntityMinecartChest.class, chest);

        registry.register(new TransportableChestOld(Blocks.TRAPPED_CHEST, -1, 2, "vanilla_trapped"));

        if (Loader.isModLoaded("ironchest"))
        {
            Block block = Block.getBlockFromName("ironchest:iron_chest");
            if (block != null && block != Blocks.AIR)
            {
                String[] names = new String[] {"iron", "gold", "diamond", "copper", "tin", "crystal", "obsidian"};
                for (int i = 0; i < 7; i++)
                {
                    registry.register(new TransportableChestOld(block, i, 3 + i, names[i]));
                }
            }
        }

        if (Loader.isModLoaded("multipagechest"))
        {
            Block block = Block.getBlockFromName("multipagechest:multipagechest");
            if (block != null && block != Blocks.AIR)
            {
                registry.register(new TransportableChestOld(block, -1, 10, "multipagechest"));
            }
        }

        /*if (Loader.isModLoaded("factorization"))
        {
            Block block = GameData.getBlockRegistry().getObject("factorization:FzBlock");
            if (block != null && block != Blocks.air)
            {
                registry.register(new FzBarrel(block, 2, 11));
            }
        }

        if (Loader.isModLoaded("varietychests"))
        {
            Block block = GameData.getBlockRegistry().getObject("varietychests:customchest");
            if (block != null && block != Blocks.air)
            {
                registry.register(new VariertyChest(block, -1, 12, false));
            }
            block = GameData.getBlockRegistry().getObject("varietychests:customglowingchest");
            if (block != null && block != Blocks.air)
            {
                registry.register(new VariertyChest(block, -1, 13, true));
            }
        }*/

        if (Loader.isModLoaded("compactstorage"))
        {
            Block block = Block.getBlockFromName("compactstorage:compactChest");
            if (block != null && block != Blocks.AIR)
            {
                registry.register(new CompactChest(block, -1, 14, "compact_chest"));
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
                    registry.register(new BasicDrawer(block, i, dv, "basic_drawer_" + names[i]));
                }
            }

            block = Block.getBlockFromName("storagedrawers:compDrawers");
            if (block != null && block != Blocks.AIR)
            {
                registry.register(new CompDrawer(block, 0, 23, "comp_drawer"));
            }
        }

        if (pickupSpawners)
        {
            registry.register(new Spawner(Blocks.MOB_SPAWNER, -1, 24, "spawner"));
        }

        if (Loader.isModLoaded("quark"))
        {
            Block block = Block.getBlockFromName("quark:custom_chest");
            if (block != null && block != Blocks.AIR)
            {
                registry.register(new QuarkChest(block, 25, "quark_chest"));
            }

            block = Block.getBlockFromName("quark:custom_chest_trap");
            if (block != null && block != Blocks.AIR)
            {
                registry.register(new QuarkChest(block, 26, "quark_chest_trap"));
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
                    registry.register(new TransportableChestOld(block, i, 28 + i, "fluidity_" + names[i]));
                }
            }
        }

        if (Loader.isModLoaded("actuallyadditions"))
        {
            Block small = Block.REGISTRY.getObject(new ResourceLocation("actuallyadditions:block_giant_chest"));
            Block medium = Block.REGISTRY.getObject(new ResourceLocation("actuallyadditions:block_giant_chest_medium"));
            Block large = Block.REGISTRY.getObject(new ResourceLocation("actuallyadditions:block_giant_chest_large"));

            if (small != null && small != Blocks.AIR)
            {
                registry.register(new StorageCrate(small, 0, 50, "crate_small"));
            }

            if (medium != null && medium != Blocks.AIR)
            {
                registry.register(new StorageCrate(medium, 0, 51, "crate_medium"));
            }

            if (large != null && large != Blocks.AIR)
            {
                registry.register(new StorageCrate(large, 0, 52, "crate_large"));
            }
        }

        if (Loader.isModLoaded("refinedrelocation"))
        {
            Block chestWood = Block.REGISTRY.getObject(new ResourceLocation("refinedrelocation:sorting_chest"));
            Block chestIron = Block.REGISTRY.getObject(new ResourceLocation("refinedrelocation:sorting_iron_chest"));

            if (chestWood != null && chestWood != Blocks.AIR)
            {
                registry.register(new SortingChestWood(chestWood, -1, "sorting_chest"));
            }

            if (chestIron != null && chestIron != Blocks.AIR)
            {
                final String[] variants = new String[] {
                        "iron", "gold", "diamond", "copper", "tin", "crystal", "obsidian", "dirt"
                };

                for (int i = 0; i < variants.length; i++)
                {
                    registry.register(new SortingChestIron(chestIron, i, "sorting_iron_chest_" + variants[i]));
                }
            }
        }

        if (Loader.isModLoaded("charsetstorage"))
        {
            Block barrel = Block.REGISTRY.getObject(new ResourceLocation("charsetstorage:barrel"));

            if (barrel != null && barrel != Blocks.AIR)
            {
                registry.register(new CharsetBarrel(barrel, 0, "charset_barrel"));
            }
        }

        if (Loader.isModLoaded("forestry"))
        {
            Block block = Block.getBlockFromName("forestry:bee_chest");
            if (block != null && block != Blocks.AIR)
            {
                registry.register(new TransportableChestImpl(block, -1, "bee_chest"));
            }
        }

        if (Loader.isModLoaded("storagedrawersextra"))
        {
            Block block = Block.getBlockFromName("storagedrawersextra:extra_drawers");
            if (block != null && block != Blocks.AIR)
            {
                String[] variants = new String[] {
                        "forestry_acacia", "forestry_balsa", "forestry_baobab", "forestry_cherry", "forestry_chestnut",
                        "forestry_citrus", "forestry_cocobolo", "forestry_ebony", "forestry_giganteum",
                        "forestry_greenheart", "forestry_ipe", "forestry_kapok", "forestry_larch",
                        "forestry_lime", "forestry_mahoe", "forestry_mahogany", "forestry_maple", "forestry_padauk",
                        "forestry_palm", "forestry_papaya", "forestry_pine", "forestry_plum", "forestry_poplar",
                        "forestry_sequoia", "forestry_teak", "forestry_walnut", "forestry_wenge", "forestry_willow",
                        "forestry_zebrawood",

                        "natura_bloodwood", "natura_darkwood", "natura_eucalyptus", "natura_fusewood",
                        "natura_ghostwood", "natura_hopseed", "natura_maple", "natura_purpleheart", "natura_redwood",
                        "natura_sakura", "natura_silverbell", "natura_tigerwood", "natura_willow",

                        "biomesoplenty_cherry", "biomesoplenty_dark", "biomesoplenty_ebony", "biomesoplenty_ethereal",
                        "biomesoplenty_eucalyptus", "biomesoplenty_fir", "biomesoplenty_hellbark",
                        "biomesoplenty_jacaranda", "biomesoplenty_magic", "biomesoplenty_mahogany",
                        "biomesoplenty_mangrove", "biomesoplenty_palm", "biomesoplenty_pine", "biomesoplenty_redwood",
                        "biomesoplenty_sacredoak", "biomesoplenty_willow",

                        "immersiveengineering_treated"
                };

                String[] names = new String[] {"full1", "full2", "full4", "half2", "half4"};
                for (int i = 0; i < names.length; i++)
                {
                    ChestRegistry.register(new BasicDrawerExtra(block, i, "extra_drawer_" + names[i], variants));
                }
            }
        }

        if (Loader.isModLoaded("tconstruct"))
        {
            Block block = Block.getBlockFromName("tconstruct:tooltables");
            if (block != null && block != Blocks.AIR)
            {
                ChestRegistry.register(new TransportableChestImpl(block, 4, "pattern_chest"));
                ChestRegistry.register(new TransportableChestImpl(block, 5, "part_chest"));
            }
        }

        if (Loader.isModLoaded("binniecore"))
        {
            Block block = Block.getBlockFromName("binniecore:storage");
            if (block != null && block != Blocks.AIR)
            {
                String[] names = new String[] {"wood", "copper", "bronze", "iron", "gold", "diamond"};
                for (int i = 0; i < names.length; i++)
                {
                    ChestRegistry.register(new StorageCompartment(block, i, names[i]));
                }
            }
        }

        if (Loader.isModLoaded("dungeontactics"))
        {
            Block block = Block.getBlockFromName("dungeontactics:barrel");
            if (block != null && block != Blocks.AIR)
            {
                ChestRegistry.register(new TransportableChestImpl(block, 0, "barrel"));
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
                    ChestRegistry.registerMinecart((Class<? extends EntityMinecartChest>) Class.forName(classNames[i]), ChestRegistry.getChestFromType(3 + i).orElse(null));

                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
