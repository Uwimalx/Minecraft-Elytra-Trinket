package net.regexion.elytra_trinket_updated;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public final class ClientTools {
	static void registerCapeRenderer() {
		LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register((state) -> {
			Minecraft client = Minecraft.getInstance();
			return client.level == null || !(client.level.getEntity(state.id) instanceof LivingEntity livingEntity)
					|| !ServerTools.isElytraTrinketEquipped(livingEntity);
		});
	}
}
