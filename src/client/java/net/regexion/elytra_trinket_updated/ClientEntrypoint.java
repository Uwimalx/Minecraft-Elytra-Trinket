package net.regexion.elytra_trinket_updated;

import net.fabricmc.api.ClientModInitializer;

public final class ClientEntrypoint implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTools.registerCapeRenderer();
	}
}
