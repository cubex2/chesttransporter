package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompactChest extends TransportableChestImpl
{
    public CompactChest(Block chestBlock, int chestMeta, int transporterDV, String name)
    {
        super(chestBlock, chestMeta, transporterDV, name);
    }

    @Override
    public NBTTagCompound modifyTileCompound(NBTTagCompound nbt, World world, BlockPos pos, EntityPlayer player, ItemStack transporter)
    {
        nbt.setInteger("facing", player.getHorizontalFacing().ordinal());

        return nbt;
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
