package dev.ftb.mods.ftbessentials.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;

public class GrindstoneMenuHelper extends GrindstoneMenu {
	public GrindstoneMenuHelper(int l, Inventory arg, final ContainerLevelAccess arg2) {
		super(l, arg, arg2);
	}

	@Override
	public boolean stillValid(Player arg) { return true; }
}
