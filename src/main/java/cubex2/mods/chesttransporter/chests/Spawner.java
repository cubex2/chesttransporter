package cubex2.mods.chesttransporter.chests;

import cubex2.mods.chesttransporter.ChestTransporter;
import cubex2.mods.chesttransporter.ItemChestTransporter;
import cubex2.mods.chesttransporter.TransporterType;
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

        if (item instanceof ItemChestTransporter)
        {
            TransporterType type = ((ItemChestTransporter) item).type;
            return ChestTransporter.canUseSpawner.containsKey(type) ? ChestTransporter.canUseSpawner.get(type) : false;
        }

        return false;
    }

    @Override
    public boolean copyTileEntity()
    {
        return true;
    }
}
