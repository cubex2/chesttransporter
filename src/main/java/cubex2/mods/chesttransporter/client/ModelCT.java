package cubex2.mods.chesttransporter.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.MultipartBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ModelCT implements IModel
{
    public static final ResourceLocation HANDLE_WOOD = new ResourceLocation("chesttransporter:item/handle_wood");
    public static final ResourceLocation HANDLE_IRON = new ResourceLocation("chesttransporter:item/handle_iron");
    public static final ResourceLocation HANDLE_GOLD = new ResourceLocation("chesttransporter:item/handle_gold");
    public static final ResourceLocation HANDLE_DIAMOND = new ResourceLocation("chesttransporter:item/handle_diamond");

    private static final ResourceLocation[] HANDLES = new ResourceLocation[]{HANDLE_WOOD, HANDLE_IRON, HANDLE_GOLD, HANDLE_DIAMOND};

    private final ResourceLocation handle;

    private final List<ResourceLocation> deps;

    public ModelCT(int handleType)
    {
        handle = HANDLES[handleType];

        deps = Lists.newArrayList();
        deps.add(handle);
        deps.addAll(ModelRegistry.getInstance().modelLocations.values());
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.copyOf(deps);
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return ImmutableList.of();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        try
        {
            IModel sub = ModelLoaderRegistry.getModel(handle);
            IBakedModel baked = sub.bake(state, format, bakedTextureGetter);

            return new BakedModelCH(baked, ModelRegistry.getInstance().bake(state, format, bakedTextureGetter));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
    }

    @Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }
}
