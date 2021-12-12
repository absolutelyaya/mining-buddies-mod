package yaya.miningbuddies.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.GameRules;
import yaya.miningbuddies.Buddies.Buddy;
import yaya.miningbuddies.Buddies.BuddyType;
import yaya.miningbuddies.Registries.BuddyManager;
import yaya.miningbuddies.accessors.PlayerEntityAccessor;

import java.util.Collection;
import java.util.Collections;

public class GiveBuddyCommand
{
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("givebuddy")
				.requires((source) -> source.hasPermissionLevel(2))
				.then(CommandManager.argument("buddy", IdentifierArgumentType.identifier())
						.suggests((context, builder) -> CommandSource.suggestIdentifiers(BuddyManager.getBuddyTypes(), builder))
						.executes((context) -> execute(context, Collections.singleton((context.getSource()).getPlayer()),
								IdentifierArgumentType.getIdentifier(context, "buddy")))
						.then(CommandManager.argument("target", EntityArgumentType.players())
								.executes((context) -> execute(context, EntityArgumentType.getPlayers(context, "target"),
										IdentifierArgumentType.getIdentifier(context, "buddy")))));
		
		dispatcher.register(literalArgumentBuilder);
	}
	
	private static void sendFeedback(ServerCommandSource source, ServerPlayerEntity player, boolean success, BuddyType b)
	{
		if(success)
		{
			if (source.getEntity() == player) {
				source.sendFeedback(new TranslatableText("commands.givebuddy.success.self", b.name), true);
			}
			else
			{
				if (source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK))
				{
					player.sendSystemMessage(new TranslatableText("buddy.get", b.name), Util.NIL_UUID);
				}
				
				source.sendFeedback(new TranslatableText("commands.givebuddy.success.other", player.getDisplayName(), b.name), true);
			}
		}
		else
		{
			if (source.getEntity() == player)
				source.sendFeedback(new TranslatableText("commands.givebuddy.fail.alreadyowned.self", b.name), true);
			else
				source.sendFeedback(new TranslatableText("commands.givebuddy.fail.alreadyowned.other", player.getDisplayName(), b.name), true);
		}
	}
	
	private static int execute(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, Identifier buddyType)
	{
		int i = 0;
		
		BuddyType b = BuddyManager.getBuddyType(buddyType);
		if(b == null)
		{
			if(context.getSource().getEntity() != null)
				context.getSource().getEntity()
						.sendSystemMessage(new TranslatableText("commands.givebuddy.fail.invalidtype", buddyType), Util.NIL_UUID);
			return 0;
		}
		for (ServerPlayerEntity serverPlayerEntity : targets)
		{
			sendFeedback(context.getSource(), serverPlayerEntity, ((PlayerEntityAccessor)serverPlayerEntity).addBuddy(new Buddy(b)), b);
			++i;
		}
		return i;
	}
}
