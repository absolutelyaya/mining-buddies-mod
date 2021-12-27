package yaya.miningbuddies.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yaya.miningbuddies.accessors.PlayerEntityAccessor;

@Mixin(NoteBlock.class)
public class MixinNoteBlock
{
	@Shadow @Final public static IntProperty NOTE;
	
	@Inject(method = "onUse", at = @At("RETURN"))
	public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
	{
		if(world.isClient)
			((PlayerEntityAccessor)player).useNoteBlock(state.get(NOTE));
	}
	
	@Inject(method = "onBlockBreakStart", at = @At("RETURN"))
	public void onBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player, CallbackInfo ci)
	{
		if(world.isClient)
			((PlayerEntityAccessor)player).useNoteBlock(state.get(NOTE));
	}
}
