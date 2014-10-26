package cubex2.mods.chesttransporter;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class FzBarrel extends TransportableChest
{
    public FzBarrel(Block chestBlock, int chestMeta, int transporterDV)
    {
        super(chestBlock, chestMeta, transporterDV);
    }

    @Override
    public ItemStack createChestStack()
    {
        Item item = Item.getItemFromBlock(getChestBlock());
        return new ItemStack(Item.getItemFromBlock(getChestBlock()), 1, 30);
    }

    @Override
    public void preRemoveChest(ItemStack transporter, TileEntity chestTE)
    {
        try
        {
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
    public void preDestroyTransporter(ItemStack transporter, TileEntity chestTE)
    {
        try
        {
            Class clazz = Class.forName("factorization.weird.TileEntityDayBarrel");
            Class typeClazz = Class.forName("factorization.weird.TileEntityDayBarrel$Type");

            NBTTagCompound logNbt = transporter.getTagCompound().getCompoundTag("WoodLog");
            NBTTagCompound slabNbt = transporter.getTagCompound().getCompoundTag("WoodSlab");
            String typeName = transporter.getTagCompound().getString("BarrelType");

            ItemStack log = ItemStack.loadItemStackFromNBT(logNbt);
            ItemStack slab = ItemStack.loadItemStackFromNBT(slabNbt);
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
    public int getIconIndex(ItemStack stack)
    {
        NBTTagCompound logNbt = stack.getTagCompound().getCompoundTag("WoodLog");
        ItemStack log = ItemStack.loadItemStackFromNBT(logNbt);

        if (log.getItem() == Item.getItemFromBlock(Blocks.log))
        {
            if (log.getItemDamage() == 0)
                return 11;
            if (log.getItemDamage() == 1)
                return 12;
            if (log.getItemDamage() == 2)
                return 13;
            if (log.getItemDamage() == 3)
                return 14;
        } else if (log.getItem() == Item.getItemFromBlock(Blocks.log2))
        {
            if (log.getItemDamage() == 0)
                return 15;
            if (log.getItemDamage() == 1)
                return 16;
        } else if (log.getItem() == Item.getItemFromBlock(Blocks.bedrock))
        {
            return 17;
        }
        return 1;
    }
}
