package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;

public class StorageCompartment extends TransportableChestImpl
{
    public StorageCompartment(Block chestBlock, int chestMeta, String name)
    {
        super(chestBlock, chestMeta, "storage_compartment_" + name);
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }
}
