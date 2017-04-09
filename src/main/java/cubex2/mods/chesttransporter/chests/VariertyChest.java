package cubex2.mods.chesttransporter.chests;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Collection;
import java.util.List;


public class VariertyChest extends TransportableChestOld
{
    private static final String[] chestTypes = new String[] {"spruce", "birch", "jungle", "acacia", "darkoak", "original"};
    private final boolean isGlow;

    public VariertyChest(Block chestBlock, int chestMeta, int transporterDV, boolean isGlow)
    {
        super(chestBlock, chestMeta, transporterDV, null);
        this.isGlow = isGlow;
    }

    @Override
    public ItemStack createChestStack(ItemStack transporter)
    {
        return new ItemStack(Item.getItemFromBlock(getChestBlock()), 1, 0);
    }

    @Override
    public void preRemoveChest(World world, BlockPos pos, EntityPlayer player, ItemStack transporter)
    {
        try
        {
            TileEntity chestTE = world.getTileEntity(pos);

            Class clazz = Class.forName("de.sanandrew.mods.varietychests.tileentity.TileEntityCustomChest");
            Class clazz1 = Class.forName("de.sanandrew.mods.varietychests.util.ChestType");

            Object chestType = ObfuscationReflectionHelper.getPrivateValue(clazz, chestTE, "chestType").toString();

            clazz1.getField("name").setAccessible(true);
            Object chestName = clazz1.getDeclaredField("name").get(chestType);


            transporter.getTagCompound().setString("VCChestType", (String) chestName);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onChestPlaced(World world, BlockPos pos, EntityPlayer player, ItemStack transporter)
    {
        try
        {
            TileEntity chestTE = world.getTileEntity(pos);
            Class clazz = Class.forName("de.sanandrew.mods.varietychests.tileentity.TileEntityCustomChest");

            String chestType = transporter.getTagCompound().getString("VCChestType");

            ObfuscationReflectionHelper.setPrivateValue(clazz, chestTE, chestType, "chestType");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public ResourceLocation getChestModel(ItemStack stack)
    {
        String chestType = stack.getTagCompound().getString("VCChestType");
        String postfix = isGlow ? "_glow" : "";
        return locationFromName("vc_" + chestType + postfix);
    }

    @Override
    public Collection<ResourceLocation> getChestModels()
    {
        List<ResourceLocation> models = Lists.newArrayList();

        String postfix = isGlow ? "_glow" : "";

        for (String chestType : chestTypes)
        {
            models.add(locationFromName("vc_" + chestType + postfix));
        }

        return models;
    }
}
