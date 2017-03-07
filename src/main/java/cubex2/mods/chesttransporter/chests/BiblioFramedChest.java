package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class BiblioFramedChest extends TransportableChest
{
    public BiblioFramedChest(Block chestBlock, int chestMeta, int transporterDV, String iconName)
    {
        super(chestBlock, chestMeta, transporterDV, iconName);
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }

    @Override
    public boolean canGrab(TileEntity chestTE)
    {
        Class<TileEntity> aClass = (Class<TileEntity>) chestTE.getClass();
        Boolean isDouble = ReflectionHelper.<Boolean, TileEntity>getPrivateValue(aClass, chestTE, "isDouble");
        return !isDouble;
    }

    @Override
    public void modifyTileCompound(EntityLivingBase living, NBTTagCompound nbt)
    {
        nbt.setInteger("angle", (living.getHorizontalFacing().getHorizontalIndex() + 5) % 4);
        nbt.setBoolean("isDouble", false);
    }
}
