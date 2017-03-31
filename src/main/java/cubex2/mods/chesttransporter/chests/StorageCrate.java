package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;

public class StorageCrate extends TransportableChestImpl
{
    public StorageCrate(Block chestBlock, int chestMeta, int transporterDV, String name)
    {
        super(chestBlock, chestMeta, transporterDV, name);
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }
}
