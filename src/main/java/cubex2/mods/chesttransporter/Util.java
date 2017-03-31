package cubex2.mods.chesttransporter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.tuple.Pair;

public class Util
{
    public static NonNullList<Pair<Integer, ItemStack>> readItemsFromNBT(NBTTagCompound tagCompound)
    {
        NonNullList<Pair<Integer, ItemStack>> items = NonNullList.create();

        NBTTagList nbtList = tagCompound.getTagList("Items", 10);

        for (int i = 0; i < nbtList.tagCount(); ++i)
        {
            NBTTagCompound nbtTagCompound = nbtList.getCompoundTagAt(i);
            NBTBase nbt = nbtTagCompound.getTag("Slot");
            int j;
            if (nbt instanceof NBTTagByte)
            {
                j = nbtTagCompound.getByte("Slot") & 255;
            } else
            {
                j = nbtTagCompound.getShort("Slot");
            }

            if (j >= 0)
            {
                ItemStack itemstack = new ItemStack(nbtTagCompound);
                items.add(Pair.of(j, itemstack));
            }
        }

        return items;
    }
}
