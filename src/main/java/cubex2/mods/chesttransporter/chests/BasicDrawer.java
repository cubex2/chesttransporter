package cubex2.mods.chesttransporter.chests;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.List;

public class BasicDrawer extends TransportableChest
{
    private static final String[] variants = new String[] {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"};

    public BasicDrawer(Block chestBlock, int chestMeta, int transporterDV, String iconName)
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

    @Override
    public Collection<ResourceLocation> getChestModels()
    {
        List<ResourceLocation> models = Lists.newArrayList();

        for (String variant : variants)
        {
            models.add(locationFromName(iconName + "_" + variant));
        }

        return models;
    }

    @Override
    public ResourceLocation getChestModel(ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ChestTile"))
        {
            NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("ChestTile");
            String mat = nbt.getString("Mat");
            if (mat == null || mat.length() == 0)
                mat = "oak";
            return locationFromName(iconName + "_" + mat);
        }
        return locationFromName(iconName + "_oak");
    }


}
