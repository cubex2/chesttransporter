package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CharsetBarrel extends TransportableChestImpl
{
    public CharsetBarrel(Block chestBlock, int chestMeta, String name)
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
        try
        {
            Class<Object> rayTraceUtils = (Class<Object>) Class.forName("pl.asie.charset.lib.utils.RayTraceUtils");
            Class<Object> spaceUtil = (Class<Object>) Class.forName("pl.asie.charset.lib.factorization.SpaceUtil");
            Class<Object> orientation = (Class<Object>) Class.forName("pl.asie.charset.lib.factorization.Orientation");

            Method getCollision = ReflectionHelper.findMethod(rayTraceUtils, null, new String[] {"getCollision"}, World.class, BlockPos.class, EntityLivingBase.class, AxisAlignedBB.class, int.class);
            Method getOrientation = ReflectionHelper.findMethod(spaceUtil, null, new String[] {"getOrientation"}, EntityLivingBase.class, EnumFacing.class, Vec3d.class);
            Method fromOrientation = ReflectionHelper.findMethod(orientation, null, new String[] {"fromDirection"}, EnumFacing.class);

            int dir = 0;

            RayTraceResult hit = (RayTraceResult) getCollision.invoke(null, world, pos, player, Block.FULL_BLOCK_AABB, 0);
            if (hit != null)
            {
                if (hit.hitVec != null)
                {
                    dir = ((Enum) getOrientation.invoke(null, player, hit.sideHit, hit.hitVec.subtract(new Vec3d(pos)))).ordinal();
                } else if (hit.sideHit != null)
                {
                    dir = ((Enum) fromOrientation.invoke(null, hit.sideHit)).ordinal();
                }
            }

            nbt.setByte("dir", (byte) dir);

        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }

        return super.modifyTileCompound(nbt, world, pos, player, transporter);
    }
}
