package cubex2.mods.chesttransporter.client;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import cubex2.mods.chesttransporter.api.TransportableChest;
import cubex2.mods.chesttransporter.chests.ChestRegistry;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

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

    public Map<ResourceLocation, IBakedModel> bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) throws Exception
    {
        Map<ResourceLocation, IBakedModel> ret = Maps.newHashMap();

        for (TransportableChest chest : ChestRegistry.getChests())
        {
            for (ResourceLocation location : chest.getChestModels())
            {
                IModel model = ModelLoaderRegistry.getModel(location);
                ret.put(location, model.bake(state, format, bakedTextureGetter));
            }
        }

        return ret;
    }
}
