package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SortingChestIron extends TransportableChestImpl
{
    public SortingChestIron(Block chestBlock, int chestMeta, String name)
    {
        super(chestBlock, chestMeta, name);
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }

    @Override
    public NBTTagCompound modifyTileCompound(NBTTagCompound nbt, World world, BlockPos pos, EntityPlayer player, ItemStack transporter)
    {
        nbt.setByte("facing", (byte) (player.getHorizontalFacing().getOpposite().getIndex()));
        return super.modifyTileCompound(nbt, world, pos, player, transporter);
    }
}
