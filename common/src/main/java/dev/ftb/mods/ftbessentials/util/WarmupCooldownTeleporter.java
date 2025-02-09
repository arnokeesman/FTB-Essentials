package dev.ftb.mods.ftbessentials.util;

import dev.ftb.mods.ftbessentials.config.FTBEConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * @author LatvianModder
 */
public class WarmupCooldownTeleporter {
	private final FTBEPlayerData playerData;
	private final ToIntFunction<ServerPlayer> cooldownConfig;
	private final ToIntFunction<ServerPlayer> warmupConfig;
	private final boolean popHistoryOnTeleport;

	private long cooldown;

	private static final Map<UUID, Warmup> WARMUPS = new HashMap<>();

	public WarmupCooldownTeleporter(FTBEPlayerData playerData, ToIntFunction<ServerPlayer> cooldownConfig, ToIntFunction<ServerPlayer> warmupConfig) {
		this(playerData, cooldownConfig, warmupConfig, false);
	}

	public WarmupCooldownTeleporter(FTBEPlayerData playerData, ToIntFunction<ServerPlayer> cooldownConfig, ToIntFunction<ServerPlayer> warmupConfig, boolean popHistoryOnTeleport) {
		this.playerData = playerData;
		this.cooldownConfig = cooldownConfig;
		this.warmupConfig = warmupConfig;
		this.popHistoryOnTeleport = popHistoryOnTeleport;
		this.cooldown = 0L;
	}

	public TeleportPos.TeleportResult checkCooldown() {
		long now = System.currentTimeMillis();

		if (now < cooldown) {
			return (TeleportPos.CooldownTeleportResult) () -> cooldown - now;
		}

		return TeleportPos.TeleportResult.SUCCESS;
	}

	public TeleportPos.TeleportResult teleport(ServerPlayer player, Function<ServerPlayer, TeleportPos> positionGetter) {
		TeleportPos.TeleportResult cooldownResult = checkCooldown();
		if (!cooldownResult.isSuccess()) {
			return cooldownResult;
		}

		int warmupTime = warmupConfig.applyAsInt(player);

		if (warmupTime == 0) {
			// just port immediately
			return teleportNow(player, positionGetter);
		} else {
			// schedule the teleport
			WARMUPS.put(player.getUUID(), new Warmup(System.currentTimeMillis() + warmupTime * 1000L, this, player.position(), positionGetter));
			return TeleportPos.TeleportResult.SUCCESS;
		}
	}

	private TeleportPos.TeleportResult teleportNow(ServerPlayer player, Function<ServerPlayer, TeleportPos> positionGetter) {
		cooldown = System.currentTimeMillis() + Math.max(0L, cooldownConfig.applyAsInt(player) * 1000L);

		TeleportPos teleportPos = positionGetter.apply(player);
		TeleportPos currentPos = new TeleportPos(player);

		TeleportPos.TeleportResult res = teleportPos.teleport(player);
		if (res.isSuccess()) {
			if (popHistoryOnTeleport) {
				playerData.popTeleportHistory();
			} else if (!FTBEConfig.BACK_ON_DEATH_ONLY.get()) {
				playerData.addTeleportHistory(player, currentPos);
			}
		}
		return res;
	}

	public static void tickWarmups(MinecraftServer server) {
		if (WARMUPS.isEmpty()) {
			return;
		}

		Set<UUID> toRemove = new HashSet<>();

		long now = System.currentTimeMillis();

		for (Map.Entry<UUID,Warmup> entry : WARMUPS.entrySet()) {
			UUID playerId = entry.getKey();
			ServerPlayer player = server.getPlayerList().getPlayer(playerId);
			if (player != null) {
				Warmup warmup = entry.getValue();
				if (warmup.when() <= now) {
					TeleportPos.TeleportResult res = warmup.teleporter().teleportNow(player, warmup.positionGetter());
					toRemove.add(playerId);
					res.runCommand(player);
				} else {
					if (player.position().distanceToSqr(warmup.initialPos) > 0.25) {
						// player has moved more than half a block
						toRemove.add(playerId);
						player.displayClientMessage(Component.literal("Teleportation interrupted!").withStyle(ChatFormatting.RED), true);
					} else {
						long seconds = (warmup.when() - now) / 1000L;
						String secStr = seconds == 1 ? "second" : "seconds";
						player.displayClientMessage(Component.literal(String.format("Teleporting in %d %s", seconds, secStr)).withStyle(ChatFormatting.YELLOW), true);
					}
				}
			} else {
				// player has probably just gone offline, just cancel it
				toRemove.add(playerId);
			}
		}

		toRemove.forEach(WARMUPS::remove);
	}

	public static void cancelWarmup(ServerPlayer player) {
		if (WARMUPS.remove(player.getUUID()) != null) {
			player.displayClientMessage(Component.literal("Teleportation interrupted!").withStyle(ChatFormatting.RED), true);
		}
	}

	private record Warmup(
			long when,
			WarmupCooldownTeleporter teleporter,
			Vec3 initialPos,
			Function<ServerPlayer,TeleportPos> positionGetter
	) { }
}