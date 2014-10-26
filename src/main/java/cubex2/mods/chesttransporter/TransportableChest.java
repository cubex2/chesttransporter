package cubex2.mods.chesttransporter;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TransportableChest
{
    protected final Block chestBlock;
    protected final int chestMeta;
    protected final int transporterDV;

    public TransportableChest(Block chestBlock, int chestMeta, int transporterDV)
    {
        this.chestBlock = chestBlock;
        this.chestMeta = chestMeta;
        this.transporterDV = transporterDV;
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

    public void preDestroyTransporter(ItemStack transporter, TileEntity chestTE)
    {
        // do nothing
    }

    public int getIconIndex(ItemStack stack)
    {
        return transporterDV;
    }
}
