package cubex2.mods.chesttransporter;

import net.minecraft.block.Block;

public class Spawner extends TransportableChest
{
    public Spawner(Block chestBlock, int chestMeta, int transporterDV, String iconName)
    {
        super(chestBlock, chestMeta, transporterDV, iconName);
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }
}
