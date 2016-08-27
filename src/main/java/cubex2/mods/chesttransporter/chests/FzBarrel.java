package cubex2.mods.chesttransporter.chests;

import cubex2.mods.chesttransporter.ChestTransporter;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class FzBarrel extends TransportableChest
{
    private static final String[] names = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "creative"};
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
    public void preDestroyTransporter(EntityLivingBase living, ItemStack transporter, TileEntity chestTE)
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

    /*@Override
    public IIcon getIcon(ItemStack stack)
    {
        NBTTagCompound logNbt = stack.getTagCompound().getCompoundTag("WoodLog");
        ItemStack log = ItemStack.loadItemStackFromNBT(logNbt);

        if (log.getItem() == Item.getItemFromBlock(Blocks.log))
        {
            if (log.getItemDamage() == 0)
                return icons[0];
            if (log.getItemDamage() == 1)
                return icons[1];
            if (log.getItemDamage() == 2)
                return icons[2];
            if (log.getItemDamage() == 3)
                return icons[3];
        } else if (log.getItem() == Item.getItemFromBlock(Blocks.log2))
        {
            if (log.getItemDamage() == 0)
                return icons[4];
            if (log.getItemDamage() == 1)
                return icons[5];
        } else if (log.getItem() == Item.getItemFromBlock(Blocks.bedrock))
        {
            return icons[6];
        }
        return icons[0];
    }

    @Override
    public void registerIcon(IIconRegister iconRegister)
    {
        String[] names = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "creative"};
        for (int i = 0; i < icons.length; i++)
        {
            icons[i] = iconRegister.registerIcon("chesttransporter:barrel_" + names[i]);
        }
    }*/

    @Override
    public String getModelName(ItemStack stack)
    {
        NBTTagCompound logNbt = stack.getTagCompound().getCompoundTag("WoodLog");
        ItemStack log = ItemStack.loadItemStackFromNBT(logNbt);

        if (log.getItem() == Item.getItemFromBlock(Blocks.LOG))
        {
            return "barrel_" + names[log.getItemDamage()];
        } else if (log.getItem() == Item.getItemFromBlock(Blocks.LOG2))
        {
            return "barrel_" + names[log.getItemDamage() + 4];
        } else if (log.getItem() == Item.getItemFromBlock(Blocks.BEDROCK))
        {
            return "barrel_creative";
        }
        return "barrel_oak";
    }

    @Override
    public void addModelLocations()
    {
        for (String name : names)
        {
            ChestTransporter.proxy.addModelLocation("barrel_" + name);
        }
    }
}
