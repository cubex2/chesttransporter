package cubex2.mods.chesttransporter;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Util
{
    public static List<Pair<Integer, ItemStack>> readItemsFromNBT(NBTTagCompound tagCompound)
    {
        List<Pair<Integer, ItemStack>> items = Lists.newArrayList();

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
                ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbtTagCompound);
                items.add(Pair.of(j, itemstack));
            }
        }

        return items;
    }
}
