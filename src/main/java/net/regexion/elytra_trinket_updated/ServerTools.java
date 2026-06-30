package net.regexion.elytra_trinket_updated;

import java.util.ArrayList;
import java.util.List;

import eu.pb4.trinkets.api.TrinketAttachment;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

/** Server- and client-side methods for Elytra Trinket. */
public final class ServerTools {
	/**
	 * Determine whether the given item stack contains a usable Elytra.
	 * 
	 * @param stack The item stack.
	 * @return Whether the given item stack contains a usable Elytra.
	 */
	private static boolean isUsableElytra(ItemStack stack) {
		return stack != null && !stack.isEmpty() && !stack.shouldBreak() && !stack.willBreakNextUse()
				&& stack.is(Items.ELYTRA);
	}

	/**
	 * Make the given entity fly if the given Elytra is usable.
	 *
	 * @param entity The entity.
	 * @param stack  The Elytra.
	 * @param doTick Whether the Elytra should be checked on this tick.
	 * @return Whether  the entity was made to fly.
	 */
	private static boolean useElytraTrinket(LivingEntity entity, ItemStack stack, boolean doTick) {
		if (!ServerTools.isUsableElytra(stack) || !(entity instanceof Player playerEntity)) {
			return false;
		}

		if (!doTick) {
			return true;
		}

		int nextRoll = entity.getGlidingTicks() + 1;
		Level world = entity.level();
		if (!world.isClientSide() && nextRoll % 10 == 0) {
			if ((nextRoll / 10) % 2 == 0) {
				stack.hurtAndBreak(1, world, playerEntity);
			}

			entity.emitGameEvent(GameEvent.ELYTRA_GLIDE);
		}

		return true;
	}

	/** Enable flight when wearing an Elytra in a back trinket slot. */
	static void registerFlight() {
		EntityElytraEvents.CUSTOM.register((entity, tickElytra) -> {
			// If an equipped Elytra is usable, fly.
			for (ItemStack stack : ServerTools.getEquippedElytraTrinkets(entity)) {
				if (ServerTools.useElytraTrinket(entity, stack, tickElytra)) {
					return true;
				}
			}

			// No usable Elytra is in a cape trinket slot.
			return false;
		});
	}

	/**
	 * Get a list of equipped Elytra trinkets.
	 * 
	 * @param entity The entity that has the Elytra equipped.
	 * @return A list of equipped Elytra trinkets.
	 */
	public static List<ItemStack> getEquippedElytraTrinkets(LivingEntity entity) {
		List<ItemStack> out = new ArrayList<>();

		TrinketAttachment attachment = TrinketsApi.getAttachment(entity);

		// Check each trinket slot with an Elytra.
		for (TrinketSlotAccess slotAccess : attachment.equipped(Items.ELYTRA, false)) {
			// Skip slots that can't hold Elytra.
			if (!slotAccess.slotType().getName().equals("back")) {
				continue;
			}

			// Skip empty stacks.
			ItemStack stack = slotAccess.get();
			if (stack == null || stack.isEmpty()) {
				continue;
			}

			out.add(stack);
		}

		return out;
	}

	/**
	 * Determine whether the given entity is wearing an Elytra in a trinket
	 * slot.
	 * 
	 * @param entity The entity to check.
	 * @return Whether the entity is wearing an Elytra in a trinket slot.
	 */
	public static boolean isElytraTrinketEquipped(LivingEntity entity) {
		return !ServerTools.getEquippedElytraTrinkets(entity).isEmpty();
	}
}
