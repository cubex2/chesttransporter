package cubex2.mods.chesttransporter.client;

import com.google.common.collect.ImmutableList;
import cubex2.mods.chesttransporter.ItemChestTransporter;
import cubex2.mods.chesttransporter.api.TransportableChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BakedModelCH implements IBakedModel
{
    public static final ModelResourceLocation location = new ModelResourceLocation("chesttransporter:smart_wood", "inventory");
    private final Map<ResourceLocation, IBakedModel> chestModels;
    private IBakedModel handle;

    private OverrideList overrides;

    private IBakedModel toUse = null;

    public BakedModelCH(IBakedModel handle, Map<ResourceLocation, IBakedModel> chestModels)
    {
        this.handle = handle;
        this.chestModels = chestModels;
        overrides = new OverrideList(this);
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
    {
        List<BakedQuad> quads = new ArrayList<BakedQuad>();
        quads.addAll(handle.getQuads(state, side, rand));
        if (toUse != null)
        {
            quads.addAll(toUse.getQuads(state, side, rand));
        }
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return handle.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return handle.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return handle.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return handle.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return overrides;
    }

    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
    {
        Optional<TransportableChest> chest = ItemChestTransporter.getChest(stack);
        if (!chest.isPresent())
        {
            toUse = null;
        } else
        {
            toUse = chestModels.get(chest.get().getChestModel(stack));
        }
        return this;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        return Pair.of(this, handle.handlePerspective(cameraTransformType).getRight());
    }

    private static class OverrideList extends ItemOverrideList
    {
        private final BakedModelCH model;

        public OverrideList(BakedModelCH model)
        {
            super(ImmutableList.<ItemOverride>of());
            this.model = model;
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
        {
            if (stack.getItem() == null || !(stack.getItem() instanceof ItemChestTransporter))
                return super.handleItemState(originalModel, stack, world, entity);

            return model.handleItemState(originalModel, stack, world, entity);
        }
    }
}
