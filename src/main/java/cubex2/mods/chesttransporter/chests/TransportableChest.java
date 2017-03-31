package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TransportableChest
{
    protected final Block chestBlock;
    protected final int chestMeta;
    protected final int transporterDV;
    protected final String iconName;

    public TransportableChest(Block chestBlock, int chestMeta, int transporterDV, String iconName)
    {
        this.chestBlock = chestBlock;
        this.chestMeta = chestMeta;
        this.transporterDV = transporterDV;
        this.iconName = iconName;
    }

    /**
     * Can the transporter stack use this chest
     *
     * @return True if it can be picked up, false if not
     */
    public boolean isUsableWith(ItemStack stack)
    {
        return true;
    }

    public boolean copyTileEntity()
    {
        return false;
    }

    public Block getChestBlock()
    {
        return chestBlock;
    }

    public int getChestMetadata()
    {
        return chestMeta;
    }

    public int getTransporterDV()
    {
        return transporterDV;
    }

    public ItemStack createChestStack(ItemStack transporter)
    {
        if (getChestMetadata() == -1)
            return new ItemStack(getChestBlock());
        return new ItemStack(getChestBlock(), 1, getChestMetadata());
    }

    public void preRemoveChest(ItemStack transporter, TileEntity chestTE)
    {
        // do nothing
    }

    public void preDestroyTransporter(EntityLivingBase living, ItemStack transporter, TileEntity chestTE)
    {
        // do nothing
    }

    public void modifyTileCompound(EntityLivingBase living, NBTTagCompound nbt)
    {
        // do nothing
    }

    public Collection<ResourceLocation> getChestModels()
    {
        return Collections.singleton(new ResourceLocation("chesttransporter:item/" + iconName));
    }

    public ResourceLocation getChestModel(ItemStack stack)
    {
        return new ResourceLocation("chesttransporter:item/" + iconName);
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag)
    {
        // do nothing
    }

    protected static ResourceLocation locationFromName(String iconName)
    {
        return new ResourceLocation("chesttransporter:item/" + iconName);
    }
}
