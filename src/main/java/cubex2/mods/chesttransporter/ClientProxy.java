package cubex2.mods.chesttransporter;

import cubex2.mods.chesttransporter.client.ModelLoaderCT;
import cubex2.mods.chesttransporter.client.ModelRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        ModelLoaderRegistry.registerLoader(new ModelLoaderCT());
    }

    @Override
    public void registerModels()
    {
        ModelResourceLocation l;

        for (TransporterType type : TransporterType.values())
        {
            Item item = ChestTransporter.items.get(type);
            l = new ModelResourceLocation("chesttransporter:smart_" + type.iconName, "inventory");
            ModelBakery.registerItemVariants(item, new ResourceLocation("chesttransporter:smart_" + type.iconName));
            ModelLoader.setCustomModelResourceLocation(item, 0, l);
        }
    }
}
