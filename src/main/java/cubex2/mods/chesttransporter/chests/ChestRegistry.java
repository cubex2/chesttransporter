package cubex2.mods.chesttransporter.chests;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cubex2.mods.chesttransporter.api.TransportableChest;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ChestRegistry
{
    private static Map<Integer, TransportableChest> dvToChest = Maps.newHashMap();

    private static Set<Class<? extends EntityMinecart>> minecarts = Sets.newHashSet();
    private static Map<Class<? extends EntityMinecart>, TransportableChest> minecartToChest = Maps.newHashMap();
    private static Map<ResourceLocation, Class<? extends EntityMinecart>> chestToMinecart = Maps.newHashMap();

    public static IForgeRegistry<TransportableChest> chestRegistry;

    public static void init()
    {
        chestRegistry = new RegistryBuilder<TransportableChest>().setName(new ResourceLocation("chesttransporter", "chests"))
                                                                 .setType(TransportableChest.class)
                                                                 .setIDRange(0, Short.MAX_VALUE)
                                                                 .add(ChestRegistry::add)
                                                                 .add((IForgeRegistry.ClearCallback<TransportableChest>) ChestRegistry::clear)
                                                                 .create();
    }

    public static Collection<TransportableChest> getChests()
    {
        return chestRegistry.getValues();
    }

    public static Optional<TransportableChest> getChestFromType(int type)
    {
        return Optional.ofNullable(dvToChest.get(type));
    }

    public static Optional<TransportableChest> getChestFromType(ResourceLocation key)
    {
        return Optional.ofNullable(chestRegistry.getValue(key));
    }

    public static void register(TransportableChest chest)
    {
        chestRegistry.register(chest);
    }

    public static void add(IForgeRegistryInternal<TransportableChest> owner, RegistryManager stage, int id, TransportableChest chest, @Nullable TransportableChest oldObj)
    {
        if (chest instanceof TransportableChestOld)
        {
            dvToChest.put(((TransportableChestOld) chest).getTransporterDV(), chest);
        }
    }

    public static void clear(IForgeRegistryInternal<TransportableChest> owner, RegistryManager stag)
    {

    }

    public static void registerMinecart(Class<? extends EntityMinecart> clazz, TransportableChest chest)
    {
        minecarts.add(clazz);
        minecartToChest.put(clazz, chest);
        chestToMinecart.put(chest.getRegistryName(), clazz);
    }

    public static boolean isSupportedMinecart(EntityMinecart minecart)
    {
        return minecarts.contains(minecart.getClass());
    }

    public static boolean isMinecartChest(TransportableChest chest)
    {
        return chestToMinecart.containsKey(chest.getRegistryName());
    }

    public static Class<? extends EntityMinecart> getMinecartClass(TransportableChest chest)
    {
        return chestToMinecart.get(chest.getRegistryName());
    }

    public static EntityMinecart createMinecart(World world, TransportableChest chest)
    {
        try
        {
            return getMinecartClass(chest).getConstructor(World.class).newInstance(world);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static ResourceLocation getChestType(EntityMinecart minecart)
    {
        return minecartToChest.get(minecart.getClass()).getRegistryName();
    }
}
