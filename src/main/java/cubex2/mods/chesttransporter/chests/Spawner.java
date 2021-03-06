package cubex2.mods.chesttransporter.chests;

import cubex2.mods.chesttransporter.ChestTransporter;
import cubex2.mods.chesttransporter.ItemChestTransporter;
import cubex2.mods.chesttransporter.TransporterType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Spawner extends TransportableChestOld
{
    public Spawner(Block chestBlock, int chestMeta, int transporterDV, String name)
    {
        super(chestBlock, chestMeta, transporterDV, name);
    }

    @Override
    public boolean canGrabChest(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack transporter)
    {
        return super.canGrabChest(world, pos, state, player, transporter) && isUsableWith(transporter);
    }

    @Override
    public boolean canPlaceChest(World world, BlockPos pos, EntityPlayer player, ItemStack transporter)
    {
        return isUsableWith(transporter);
    }

    private boolean isUsableWith(ItemStack stack)
    {
        Item item = stack.getItem();

        if (item instanceof ItemChestTransporter)
        {
            TransporterType type = ((ItemChestTransporter) item).type;
            return ChestTransporter.canUseSpawner.containsKey(type) ? ChestTransporter.canUseSpawner.get(type) : false;
        }

        return false;
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag advanced)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound chestTile = stack.getTagCompound().getCompoundTag("ChestTile");
            NBTTagCompound spawnData = chestTile.getCompoundTag("SpawnData");
            String mobId = spawnData.getString("id");

            if (mobId.length() > 0)
            {
                String translated = EntityList.getTranslationName(new ResourceLocation(mobId));
                list.add(translated == null ? mobId : translated);
            }
        }
    }
}
