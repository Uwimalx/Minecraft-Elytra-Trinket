package net.regexion.elytra_trinket_updated;

import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.BipedRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.core.component.DataComponentTypes;
import net.minecraft.core.component.EquippableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.EquipmentAsset;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.EquipmentRenderer;
import net.minecraft.client.renderer.entity.equipment.EquipmentModel;

/**
 * A feature renderer for an Elytra trinket. This class is almost functionally
 * identical to
 * {@link net.minecraft.client.render.entity.feature.ElytraFeatureRenderer}.
 */
public class ElytraTrinketFeatureRenderer<S extends BipedRenderState, M extends EntityModel<S>>
		extends RenderLayer<S, M> {
	private final ElytraModel model;
	private final ElytraModel babyModel;
	private final EquipmentRenderer renderer;

	public ElytraTrinketFeatureRenderer(RenderLayerParent<S, M> context, EquipmentRenderer renderer) {
		super(context);
		this.model = new ElytraModel(ModelLayers.ELYTRA);
		this.babyModel = new ElytraModel(ModelLayers.ELYTRA_BABY);
		this.renderer = renderer;
	}

	@Override
	public void submit(PoseStack matrices, SubmitNodeCollector vertices, int light, S state, float limbAngle,
                       float limbDistance) {
		if (!(state instanceof PlayerRenderState playerState)) {
			return;
		}

		Minecraft client = Minecraft.getInstance();
		if (client.level == null) {
			return;
		}

		Entity entity = client.level.getEntityById(playerState.id);
		if (!(entity instanceof LivingEntity livingEntity)) {
			return;
		}

		List<ItemStack> stacks = ServerTools.getEquippedElytraTrinkets(livingEntity);
		if (stacks.isEmpty()) {
			return;
		}
		ItemStack stack = stacks.getFirst();

		EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
		if (equippableComponent == null) {
			return;
		}
		Optional<ResourceKey<EquipmentAsset>> optionalAssetId = equippableComponent.assetId();
		if (optionalAssetId.isEmpty()) {
			return;
		}
		ResourceKey<EquipmentAsset> assetId = optionalAssetId.get();

		Identifier identifier = null;
		Identifier elytraTexture = null;
        Identifier capeTexture = null;
        if(playerState.skinTextures.elytra() != null){
            elytraTexture = playerState.skinTextures.elytra().texturePath();
        }
		if (elytraTexture != null) {
			identifier = elytraTexture;
		} else if (playerState.capeVisible) {
			if(playerState.skinTextures.cape() != null){
                capeTexture = playerState.skinTextures.cape().texturePath();
            }
			if (capeTexture != null) {
				identifier = capeTexture;
			}
		}
		ElytraModel model = state.baby ? this.babyModel : this.model;

		matrices.pushPose();
		matrices.translate(0, 0, 0.125);
		model.setupAnim(state);
		this.renderer.render(EquipmentModel.LayerType.WINGS, assetId, model, state, stack, matrices, vertices, light,
				identifier, state.outlineColor, 0);
		matrices.popPose();
	}
}
