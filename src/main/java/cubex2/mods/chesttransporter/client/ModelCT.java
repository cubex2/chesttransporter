package cubex2.mods.chesttransporter.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ModelCT implements IModel
{
    public static final ResourceLocation TEXTURE = new ResourceLocation("chesttransporter:items/ct_wood");
    public static final ResourceLocation TEXTURE2 = new ResourceLocation("chesttransporter:items/vanilla");

    public static final ModelResourceLocation HANDLE_WOOD = new ModelResourceLocation("chesttransporter:item/handle_wood", "inventory");
    public static final ModelResourceLocation HANDLE_IRON = new ModelResourceLocation("chesttransporter:item/handle_iron", "inventory");
    public static final ModelResourceLocation HANDLE_GOLD = new ModelResourceLocation("chesttransporter:item/handle_gold", "inventory");
    public static final ModelResourceLocation HANDLE_DIAMOND = new ModelResourceLocation("chesttransporter:item/handle_diamond", "inventory");

    private static final ModelResourceLocation[] HANDLES = new ModelResourceLocation[]{HANDLE_WOOD, HANDLE_IRON, HANDLE_GOLD, HANDLE_DIAMOND};

    private final ModelResourceLocation handle;

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
        //return ImmutableList.of();
        return ImmutableList.copyOf(deps);
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return ImmutableList.of();
        //return ImmutableList.copyOf(new ResourceLocation[]{TEXTURE, TEXTURE2});
    }

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        try
        {
            IModel sub = ModelLoaderRegistry.getModel(handle);
            IBakedModel baked = sub.bake(state, format, bakedTextureGetter);

            return new BakedModelCH(baked, ModelRegistry.getInstance().bake(state, format, bakedTextureGetter));
        } catch (IOException e)
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
