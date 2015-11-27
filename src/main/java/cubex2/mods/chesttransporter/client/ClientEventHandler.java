package cubex2.mods.chesttransporter.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEventHandler
{
    public static TextureAtlasSprite texture;

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event)
    {
        /*Object object = event.modelRegistry.getObject(BakedModelTwoParts.location);
        if (object instanceof IBakedModel)
        {
            IBakedModel existing = (IBakedModel) object;
            BakedModelTwoParts custom = new BakedModelTwoParts(existing, existing);
            event.modelRegistry.putObject(BakedModelTwoParts.location, custom);
        }*/
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event)
    {
        texture = event.map.registerSprite(new ResourceLocation("chesttransporter:items/vanilla"));
    }
}
