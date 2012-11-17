package cubex2.mods.chesttransporter.client;

import net.minecraftforge.client.MinecraftForgeClient;
import cubex2.mods.chesttransporter.CommonProxy;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderInformation() {
		MinecraftForgeClient.preloadTexture("/cubex2/mods/chesttransporter/client/textures/textures.png");
	}
}
