package cubex2.mods.chesttransporter.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cubex2.mods.chesttransporter.TransporterType;
import cubex2.mods.chesttransporter.api.TransportableChest;
import cubex2.mods.chesttransporter.chests.ChestRegistry;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

public class ModelCT implements IModel
{
    private static final EnumMap<TransporterType, ResourceLocation> handles = Maps.newEnumMap(TransporterType.class);

    static
    {
        for (TransporterType type : TransporterType.values())
        {
            handles.put(type, new ResourceLocation("chesttransporter:item/handle_" + type.iconName));
        }
    }

    private final ResourceLocation handle;

    private final List<ResourceLocation> deps;

    public ModelCT(TransporterType type)
    {
        handle = handles.get(type);

        deps = Lists.newArrayList();
        deps.add(handle);

        for (TransportableChest chest : ChestRegistry.getChests())
        {
            deps.addAll(chest.getChestModels());
        }
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
