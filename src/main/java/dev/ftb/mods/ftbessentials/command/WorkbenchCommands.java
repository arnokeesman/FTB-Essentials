package dev.ftb.mods.ftbessentials.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.ftb.mods.ftbessentials.config.FTBEConfig;
import dev.ftb.mods.ftbessentials.screen.CraftingMenuHelper;
import dev.ftb.mods.ftbessentials.screen.GrindstoneMenuHelper;
import dev.ftb.mods.ftbessentials.screen.SmithingMenuHelper;
import dev.ftb.mods.ftbessentials.screen.StonecutterMenuHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.*;

public class WorkbenchCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		if (FTBEConfig.CRAFT.isEnabled()) {
			dispatcher.register(Commands.literal("craft")
					.requires(FTBEConfig.CRAFT.enabledAndOp())
					.executes(context -> craft(context.getSource().getPlayerOrException())));
		}

		if (FTBEConfig.SMITHING.isEnabled()) {
			dispatcher.register(Commands.literal("smithing")
					.requires(FTBEConfig.SMITHING.enabledAndOp())
					.executes(context -> smithing(context.getSource().getPlayerOrException())));
			dispatcher.register(Commands.literal("st")
					.requires(FTBEConfig.SMITHING.enabledAndOp())
					.executes(context -> smithing(context.getSource().getPlayerOrException())));
		}

		if (FTBEConfig.GRINDSTONE.isEnabled()) {
			dispatcher.register(Commands.literal("grindstone")
					.requires(FTBEConfig.GRINDSTONE.enabledAndOp())
					.executes(context -> grind(context.getSource().getPlayerOrException())));
			dispatcher.register(Commands.literal("gs")
					.requires(FTBEConfig.GRINDSTONE.enabledAndOp())
					.executes(context -> grind(context.getSource().getPlayerOrException())));
		}

		if (FTBEConfig.STONECUTTER.isEnabled()) {
			dispatcher.register(Commands.literal("stonecutter")
					.requires(FTBEConfig.STONECUTTER.enabledAndOp())
					.executes(context -> cutStone(context.getSource().getPlayerOrException())));
			dispatcher.register(Commands.literal("sc")
					.requires(FTBEConfig.STONECUTTER.enabledAndOp())
					.executes(context -> cutStone(context.getSource().getPlayerOrException())));
		}
	}

	private static int craft(ServerPlayer player) {
		player.openMenu(new SimpleMenuProvider(
				(i, inv, p) -> new CraftingMenuHelper(i, inv, ContainerLevelAccess.create(player.level, player.blockPosition())),
				new TranslatableComponent("container.crafting")));
		return 1;
	}

	private static int smithing(ServerPlayer player) {
		player.openMenu(new SimpleMenuProvider(
				(i, inv, p) -> new SmithingMenuHelper(i, inv, ContainerLevelAccess.create(player.level, player.blockPosition())),
				new TranslatableComponent("container.upgrade")));
		return 1;
	}

	private static int grind(ServerPlayer player) {
		player.openMenu(new SimpleMenuProvider(
				(i, inv, p) -> new GrindstoneMenuHelper(i, inv, ContainerLevelAccess.create(player.level, player.blockPosition())),
				new TranslatableComponent("container.grindstone_title")));
		return 1;
	}

	private static int cutStone(ServerPlayer player) {
		player.openMenu(new SimpleMenuProvider(
				(i, inv, p) -> new StonecutterMenuHelper(i, inv, ContainerLevelAccess.create(player.level, player.blockPosition())),
				new TranslatableComponent("container.stonecutter")));
		return 1;
	}
}
