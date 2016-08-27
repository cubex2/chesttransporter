package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class CompactChest extends TransportableChest
{
    public CompactChest(Block chestBlock, int chestMeta, int transporterDV, String iconName)
    {
        super(chestBlock, chestMeta, transporterDV, iconName);
    }

    @Override
    public void preDestroyTransporter(EntityLivingBase living, ItemStack transporter, TileEntity chestTE)
    {

    }

    @Override
    public void preRemoveChest(ItemStack transporter, TileEntity chestTE)
    {

    }

    @Override
    public void modifyTileCompound(EntityLivingBase living, NBTTagCompound nbt)
    {
        nbt.setInteger("facing", living.getHorizontalFacing().ordinal());
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }

    @Override
    public ItemStack createChestStack(ItemStack transporter)
    {
        ItemStack stack = super.createChestStack(transporter);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setIntArray("size", new int[2]);
        stack.setTagCompound(nbt);
        return stack;
    }
}
