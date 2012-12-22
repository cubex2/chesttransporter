package cubex2.mods.chesttransporter.client;

import cubex2.mods.chesttransporter.CommonProxy;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderInformation() {
		MinecraftForgeClient.preloadTexture("/cubex2/mods/chesttransporter/client/textures/textures.png");
	}
}
