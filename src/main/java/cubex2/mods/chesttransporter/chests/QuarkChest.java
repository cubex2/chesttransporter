package cubex2.mods.chesttransporter.chests;

import cubex2.mods.chesttransporter.ChestTransporter;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

public class QuarkChest extends TransportableChest
{
    private static final String[] variants = new String[] {"spruce", "birch", "jungle", "acacia", "dark_oak"};

    public QuarkChest(Block chestBlock, int transporterDV, String iconName)
    {
        super(chestBlock, -1, transporterDV, iconName);
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
        String type = transporter.getTagCompound().getCompoundTag("ChestTile").getString("type");
        stack.setItemDamage(ArrayUtils.indexOf(variants, type));
        return stack;
    }

    @Override
    public String getModelName(ItemStack stack)
    {
        String type = stack.getTagCompound().getCompoundTag("ChestTile").getString("type");
        return iconName + "_" + type;
    }

    @Override
    public void addModelLocations()
    {
        for (String variant : variants)
        {
            ChestTransporter.proxy.addModelLocation("quark_chest_" + variant);
        }
    }
}
