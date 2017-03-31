package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompDrawer extends TransportableChestImpl
{
    public CompDrawer(Block chestBlock, int chestMeta, int transporterDV, String name)
    {
        super(chestBlock, chestMeta, transporterDV, name);
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }

    @Override
    public NBTTagCompound modifyTileCompound(NBTTagCompound nbt, World world, BlockPos pos, EntityPlayer player, ItemStack transporter)
    {
        nbt.setByte("Dir", (byte) player.getHorizontalFacing().getOpposite().ordinal());

        return nbt;
    }
}
