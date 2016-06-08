package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

public class CompDrawer extends TransportableChest
{
    public CompDrawer(Block chestBlock, int chestMeta, int transporterDV, String iconName)
    {
        super(chestBlock, chestMeta, transporterDV, iconName);
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }

    @Override
    public void modifyTileCompound(EntityLivingBase living, NBTTagCompound nbt)
    {
        nbt.setByte("Dir", (byte) living.getHorizontalFacing().getOpposite().ordinal());
    }
}
