package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class BiblioFramedChest extends TransportableChestImpl
{
    public BiblioFramedChest(Block chestBlock, int chestMeta, int transporterDV, String name)
    {
        super(chestBlock, chestMeta, transporterDV, name);
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }

    @Override
    public boolean canGrabChest(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack transporter)
    {
        if (!super.canGrabChest(world, pos, state, player, transporter))
            return false;

        TileEntity chestTE = world.getTileEntity(pos);
        Class<TileEntity> aClass = (Class<TileEntity>) chestTE.getClass();
        Boolean isDouble = ReflectionHelper.<Boolean, TileEntity>getPrivateValue(aClass, chestTE, "isDouble");
        return !isDouble;
    }

    @Override
    public NBTTagCompound modifyTileCompound(NBTTagCompound nbt, World world, BlockPos pos, EntityPlayer player, ItemStack transporter)
    {
        nbt.setInteger("angle", (player.getHorizontalFacing().getHorizontalIndex() + 5) % 4);
        nbt.setBoolean("isDouble", false);

        return nbt;
    }
}
