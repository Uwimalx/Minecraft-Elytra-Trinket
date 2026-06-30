package net.regexion.elytra_trinket_updated;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRendererCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;

public final class ClientTools {
	static void registerCapeRenderer() {
		LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register((state) -> {
			Minecraft client = Minecraft.getInstance();
			return client.level == null || !(client.level.getEntityById(state.id) instanceof LivingEntity livingEntity)
					|| !ServerTools.isElytraTrinketEquipped(livingEntity);
		});
	}

	static void registerRenderer() {
		LivingEntityRendererCallback.EVENT.register((type, renderer, context) -> {
			if (!(renderer instanceof PlayerRenderer playerRenderer)) {
				return;
			}

			renderer.addLayer(new ElytraTrinketFeatureRenderer<>(playerRenderer, context.getEquipmentRenderer()));
		});
	}
}
