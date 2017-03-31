package cubex2.mods.chesttransporter.chests;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Collection;
import java.util.List;

public class FzBarrel extends TransportableChestImpl
{
    private static final String[] names = new String[] {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "creative"};
    //private final IIcon[] icons = new IIcon[7];

    public FzBarrel(Block chestBlock, int chestMeta, int transporterDV)
    {
        super(chestBlock, chestMeta, transporterDV, null);
    }

    @Override
    public ItemStack createChestStack(ItemStack transporter)
    {
        return new ItemStack(Item.getItemFromBlock(getChestBlock()), 1, 30);
    }

    @Override
    public void preRemoveChest(World world, BlockPos pos, EntityPlayer player, ItemStack transporter)
    {
        try
        {
            TileEntity chestTE = world.getTileEntity(pos);
            Class clazz = Class.forName("factorization.weird.TileEntityDayBarrel");

            Object log = ObfuscationReflectionHelper.getPrivateValue(clazz, chestTE, "woodLog");
            Object slab = ObfuscationReflectionHelper.getPrivateValue(clazz, chestTE, "woodSlab");
            Object type = ObfuscationReflectionHelper.getPrivateValue(clazz, chestTE, "type");

            NBTTagCompound logNbt = new NBTTagCompound();
            ((ItemStack) log).writeToNBT(logNbt);
            NBTTagCompound slabNbt = new NBTTagCompound();
            ((ItemStack) slab).writeToNBT(slabNbt);

            transporter.getTagCompound().setTag("WoodLog", logNbt);
            transporter.getTagCompound().setTag("WoodSlab", slabNbt);
            transporter.getTagCompound().setString("BarrelType", type.toString());
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onChestPlaced(World world, BlockPos pos, EntityPlayer player, ItemStack transporter)
    {
        try
        {
            TileEntity chestTE = world.getTileEntity(pos);
            Class clazz = Class.forName("factorization.weird.TileEntityDayBarrel");
            Class typeClazz = Class.forName("factorization.weird.TileEntityDayBarrel$Type");

            NBTTagCompound logNbt = transporter.getTagCompound().getCompoundTag("WoodLog");
            NBTTagCompound slabNbt = transporter.getTagCompound().getCompoundTag("WoodSlab");
            String typeName = transporter.getTagCompound().getString("BarrelType");

            ItemStack log = new ItemStack(logNbt);
            ItemStack slab = new ItemStack(slabNbt);
            Object type = null;
            try
            {
                type = typeClazz.getDeclaredMethod("valueOf", String.class).invoke(null, typeName);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            ObfuscationReflectionHelper.setPrivateValue(clazz, chestTE, log, "woodLog");
            ObfuscationReflectionHelper.setPrivateValue(clazz, chestTE, slab, "woodSlab");
            ObfuscationReflectionHelper.setPrivateValue(clazz, chestTE, type, "type");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public ResourceLocation getChestModel(ItemStack stack)
    {
        NBTTagCompound logNbt = stack.getTagCompound().getCompoundTag("WoodLog");
        ItemStack log = new ItemStack(logNbt);

        if (log.getItem() == Item.getItemFromBlock(Blocks.LOG))
        {
            return locationFromName("barrel_" + names[log.getItemDamage()]);
        } else if (log.getItem() == Item.getItemFromBlock(Blocks.LOG2))
        {
            return locationFromName("barrel_" + names[log.getItemDamage() + 4]);
        } else if (log.getItem() == Item.getItemFromBlock(Blocks.BEDROCK))
        {
            return locationFromName("barrel_creative");
        }
        return locationFromName("barrel_oak");
    }

    @Override
    public Collection<ResourceLocation> getChestModels()
    {
        List<ResourceLocation> models = Lists.newArrayList();

        for (String name : names)
        {
            models.add(locationFromName("barrel_" + name));
        }

        return models;
    }
}
