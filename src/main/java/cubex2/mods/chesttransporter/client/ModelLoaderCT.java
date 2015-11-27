package cubex2.mods.chesttransporter.client;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

public class ModelLoaderCT implements ICustomModelLoader
{
    @Override
    public boolean accepts(ResourceLocation l)
    {
        return l.getResourceDomain().equals("chesttransporter")
                && l.getResourcePath().contains("smart");
    }

    @Override
    public IModel loadModel(ResourceLocation l) throws IOException
    {
        String type = l.getResourcePath().substring(l.getResourcePath().lastIndexOf('_') + 1);
        return new ModelCT(ArrayUtils.indexOf(new String[]{"wood", "iron", "gold", "diamond"}, type));
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {

    }
}
