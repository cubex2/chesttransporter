package cubex2.mods.chesttransporter;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Spawner extends TransportableChest
{
    public Spawner(Block chestBlock, int chestMeta, int transporterDV, String iconName)
    {
        super(chestBlock, chestMeta, transporterDV, iconName);
    }

    @Override
    public boolean isUsableWith(ItemStack stack)
    {
        Item item = stack.getItem();

        if (item == ChestTransporter.chestTransporter)
            return ChestTransporter.spawnerWithWood;

        if (item == ChestTransporter.chestTransporterIron)
            return ChestTransporter.spawnerWithIron;

        if (item == ChestTransporter.chestTransporterGold)
            return ChestTransporter.spawnerWithGold;

        if (item == ChestTransporter.chestTransporterDiamond)
            return ChestTransporter.spawnerWithDiamond;

        return false;
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }
}
