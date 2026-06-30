package net.regexion.elytra_trinket_updated;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.player.PlayerEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

/** Client-side methods for Elytra Trinket. */
public final class ClientTools {
	/** Disable rendering capes when wearing an Elytra in a back trinket slot. */
	static void registerCapeRenderer() {
		LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register((state) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			return client.world == null || !(client.world.getEntityById(state.id) instanceof LivingEntity livingEntity)
					|| !ServerTools.isElytraTrinketEquipped(livingEntity);
		});
	}

	/** Enable rendering Elytra when wearing an Elytra in a back trinket slot. */
	static void registerRenderer() {
		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((type, renderer, registrar, context) -> {
			if (!(renderer instanceof PlayerEntityRenderer playerRenderer)) {
				return;
			}

			registrar.register(new ElytraTrinketFeatureRenderer<>(playerRenderer, context.getEntityModels(),
					context.getEquipmentRenderer()));
		});
	}
}
