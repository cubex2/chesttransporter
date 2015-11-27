package cubex2.mods.chesttransporter.client;

import com.google.common.collect.ImmutableList;
import cubex2.mods.chesttransporter.ChestRegistry;
import cubex2.mods.chesttransporter.ItemChestTransporter;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartItemModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BakedModelCH implements ISmartItemModel, IFlexibleBakedModel, IPerspectiveAwareModel
{
    public static final ModelResourceLocation location = new ModelResourceLocation("chesttransporter:smart_wood", "inventory");
    private final Map<String, IBakedModel> chestModels;
    private IBakedModel handle;

    private IBakedModel toUse;

    public BakedModelCH(IBakedModel handle, Map<String, IBakedModel> chestModels)
    {
        this.handle = handle;
        this.chestModels = chestModels;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack)
    {
        int chestType = ItemChestTransporter.getTagCompound(stack).getByte("ChestType");
        if (chestType == 0) toUse = null;
        else toUse = chestModels.get(ChestRegistry.dvToChest.get(chestType).getModelName(stack));
        return this;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing side)
    {
        return ImmutableList.of();
    }

    @Override
    public List<BakedQuad> getGeneralQuads()
    {
        List<BakedQuad> quads = new ArrayList<BakedQuad>();
        quads.addAll(handle.getGeneralQuads());
        if (toUse != null)
        {
            quads.addAll(toUse.getGeneralQuads());
        }
        return quads;
    }

    @Override
    public VertexFormat getFormat()
    {
        return ((IFlexibleBakedModel) handle).getFormat();
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
    public TextureAtlasSprite getTexture()
    {
        return handle.getTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return handle.getItemCameraTransforms();
    }


    @Override
    public Pair<IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        return Pair.of((IBakedModel) this, ((IPerspectiveAwareModel) handle).handlePerspective(cameraTransformType).getRight());
    }
}
