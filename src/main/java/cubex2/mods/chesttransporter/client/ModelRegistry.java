package cubex2.mods.chesttransporter.client;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.io.IOException;
import java.util.Map;

public class ModelRegistry
{
    private static ModelRegistry ourInstance = new ModelRegistry();

    public static ModelRegistry getInstance()
    {
        return ourInstance;
    }

    private ModelRegistry()
    {
    }

    public Map<String, ModelResourceLocation> modelLocations = Maps.newHashMap();

    public Map<String, IBakedModel> bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) throws IOException
    {
        Map<String, IBakedModel> ret = Maps.newHashMap();

        for (Map.Entry<String, ModelResourceLocation> entry : modelLocations.entrySet())
        {
            IModel model = ModelLoaderRegistry.getModel(entry.getValue());
            ret.put(entry.getKey(), model.bake(state, format, bakedTextureGetter));
        }

        return ret;
    }
}
