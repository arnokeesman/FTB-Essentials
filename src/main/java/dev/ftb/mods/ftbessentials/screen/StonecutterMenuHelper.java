package dev.ftb.mods.ftbessentials.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.StonecutterMenu;

public class StonecutterMenuHelper extends StonecutterMenu {
	public StonecutterMenuHelper(int i, Inventory arg, ContainerLevelAccess arg2) {
		super(i, arg, arg2);
	}

	@Override
	public boolean stillValid(Player arg) { return true; }
}
