package cubex2.mods.chesttransporter.chests;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.List;

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
    public ResourceLocation getChestModel(ItemStack stack)
    {
        String type = stack.getTagCompound().getCompoundTag("ChestTile").getString("type");
        return locationFromName(iconName + "_" + type);
    }

    @Override
    public Collection<ResourceLocation> getChestModels()
    {
        List<ResourceLocation> models = Lists.newArrayList();

        for (String variant : variants)
        {
            models.add(locationFromName("quark_chest_" + variant));
        }

        return models;
    }
}
