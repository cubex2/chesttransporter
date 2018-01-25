package cubex2.mods.chesttransporter.chests;

import net.minecraft.block.Block;

public class YabbaBarrel extends TransportableChestImpl
{
    public YabbaBarrel(Block chestBlock, int chestMeta, String name)
    {
        super(chestBlock, chestMeta, name);
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }
}
