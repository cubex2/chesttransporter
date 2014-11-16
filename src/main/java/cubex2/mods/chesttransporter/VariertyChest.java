package cubex2.mods.chesttransporter;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class VariertyChest extends TransportableChest
{
    private final boolean isGlow;
    private final IIcon[] icons;

    public VariertyChest(Block chestBlock, int chestMeta, int transporterDV, boolean isGlow)
    {
        super(chestBlock, chestMeta, transporterDV, null);
        this.isGlow = isGlow;
        icons = new IIcon[isGlow ? 6 : 5];
    }

    @Override
    public ItemStack createChestStack()
    {
        return new ItemStack(Item.getItemFromBlock(getChestBlock()), 1, 0);
    }

    @Override
    public void preRemoveChest(ItemStack transporter, TileEntity chestTE)
    {
        try
        {
            Class clazz = Class.forName("de.sanandrew.mods.varietychests.tileentity.TileEntityCustomChest");

            String chestType = (String) ObfuscationReflectionHelper.getPrivateValue(clazz, chestTE, "chestType");

            transporter.getTagCompound().setString("VCChestType", chestType);
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void preDestroyTransporter(ItemStack transporter, TileEntity chestTE)
    {
        try
        {
            Class clazz = Class.forName("de.sanandrew.mods.varietychests.tileentity.TileEntityCustomChest");

            String chestType = transporter.getTagCompound().getString("VCChestType");

            ObfuscationReflectionHelper.setPrivateValue(clazz, chestTE, chestType, "chestType");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public IIcon getIcon(ItemStack stack)
    {
        String chestType = stack.getTagCompound().getString("VCChestType");
        if (chestType.equals("spruce"))
            return icons[0];
        if (chestType.equals("birch"))
            return icons[1];
        if (chestType.equals("jungle"))
            return icons[2];
        if (chestType.equals("acacia"))
            return icons[3];
        if (chestType.equals("darkoak"))
            return icons[4];
        if (chestType.equals("original"))
            return icons[5];

        return icons[0];
    }

    @Override
    public void registerIcon(IIconRegister iconRegister)
    {
        String[] chestTypes = new String[]{"spruce", "birch", "jungle", "acacia", "darkoak", "original"};
        for (int i = 0; i < icons.length; i++)
        {
            icons[i] = iconRegister.registerIcon("chesttransporter:vc_" + chestTypes[i] + (isGlow ? "_glow" : ""));
        }
    }
}
