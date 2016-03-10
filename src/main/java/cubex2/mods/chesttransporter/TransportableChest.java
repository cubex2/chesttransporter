package cubex2.mods.chesttransporter;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

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
        addModelLocations();
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

    public ItemStack createChestStack()
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

    }

    public String getModelName(ItemStack stack)
    {
        return iconName;
    }

    public void addModelLocations()
    {
        ChestTransporter.proxy.addModelLocation(iconName);
    }
}
