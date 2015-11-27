package cubex2.mods.chesttransporter;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;


public class VariertyChest extends TransportableChest
{
    private static final String[] chestTypes = new String[]{"spruce", "birch", "jungle", "acacia", "darkoak", "original"};
    private final boolean isGlow;

    public VariertyChest(Block chestBlock, int chestMeta, int transporterDV, boolean isGlow)
    {
        super(chestBlock, chestMeta, transporterDV, null);
        this.isGlow = isGlow;
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
    public String getModelName(ItemStack stack)
    {
        String chestType = stack.getTagCompound().getString("VCChestType");
        String postfix = isGlow ? "_glow" : "";
        return "vc_" + chestType + postfix;
    }

    @Override
    public void addModelLocations()
    {
        String postfix = isGlow ? "_glow" : "";

        for (String chestType : chestTypes)
        {
            ChestTransporter.proxy.addModelLocation("vc_" + chestType + postfix);
        }
    }

    /*@Override
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
    }*/
}
