package cubex2.mods.chesttransporter.chests;

import cubex2.mods.chesttransporter.ChestTransporter;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BasicDrawer extends TransportableChest
{
    private static final String[] variants = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"};

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
    public String getModelName(ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ChestTile"))
        {
            NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("ChestTile");
            String mat = nbt.getString("Mat");
            if (mat == null || mat.length() == 0)
                mat = "oak";
            return iconName + "_" + mat;
        }
        return iconName + "_oak";
    }

    @Override
    public void addModelLocations()
    {
        for (String variant : variants)
        {
            ChestTransporter.proxy.addModelLocation(iconName + "_" + variant);
        }
    }
}
