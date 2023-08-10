package dev.ftb.mods.ftbessentials.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

public class CraftingMenuHelper extends CraftingMenu {
	public CraftingMenuHelper(int i, Inventory arg, ContainerLevelAccess arg2) {
		super(i, arg, arg2);
	}

	@Override
	public boolean stillValid(Player arg) { return true; }
}
