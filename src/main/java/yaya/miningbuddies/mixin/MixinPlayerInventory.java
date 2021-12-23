package yaya.miningbuddies.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yaya.miningbuddies.Buddies.Reaction;
import yaya.miningbuddies.client.MiningBuddiesClientMod;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory
{
	@Inject(method = "addStack(Lnet/minecraft/item/ItemStack;)I", at = @At("RETURN"))
	void onPickup(ItemStack stack, CallbackInfoReturnable<Integer> cir)
	{
		MiningBuddiesClientMod.BUDDY_MINI_HUD.updateReaction(Reaction.ReactionTrigger.PICKUP, Registry.ITEM.getId(stack.getItem()).toString(), stack.getCount());
		System.out.println(Registry.ITEM.getId(stack.getItem()));
	}
}
