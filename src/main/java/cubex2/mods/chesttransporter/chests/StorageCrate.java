package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;

public class StorageCrate extends TransportableChest
{
    public StorageCrate(Block chestBlock, int chestMeta, int transporterDV, String iconName)
    {
        super(chestBlock, chestMeta, transporterDV, iconName);
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }
}
