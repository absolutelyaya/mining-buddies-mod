package yaya.miningbuddies.mixin;

import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yaya.miningbuddies.Registries.BuddyManager;

@Mixin(ServerResourceManager.class)
public class MixinServerResourceManager
{
	@Shadow @Final private ReloadableResourceManager resourceManager;
	@Unique private BuddyManager buddyManager;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void onReloadResources(DynamicRegistryManager registryManager, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel, CallbackInfo ci)
	{
		this.buddyManager = new BuddyManager();
		this.resourceManager.registerReloader(this.buddyManager);
	}
}
