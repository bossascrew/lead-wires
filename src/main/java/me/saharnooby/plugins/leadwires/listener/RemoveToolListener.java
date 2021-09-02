package me.saharnooby.plugins.leadwires.listener;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.Tools;
import me.saharnooby.plugins.leadwires.evens.LeadRemoveEvent;
import me.saharnooby.plugins.leadwires.wire.Vector;
import me.saharnooby.plugins.leadwires.wire.Wire;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author saharNooby
 * @since 16:25 25.03.2020
 */
public final class RemoveToolListener implements Listener {

	@EventHandler
	public void onRemovedUse(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		if (!Tools.isRemoveTool(e.getItem())) {
			return;
		}

		e.setCancelled(true);

		Player player = e.getPlayer();

		if (!player.hasPermission("leadwires.use-tools")) {
			LeadWires.sendMessage(player, "noPermissionsForTool");
			return;
		}

		Set<UUID> toRemove = new HashSet<>();

		Location loc = e.getClickedBlock().getLocation();

		for (Wire wire: LeadWires.getApi().getWires().values()) {
			if (wire.getWorld().equals(loc.getWorld().getName()) && (isInBlock(loc, wire.getA()) || isInBlock(loc, wire.getB()))) {
				toRemove.add(wire.getUuid());
			}
		}

		if (toRemove.isEmpty()) {
			LeadWires.sendMessage(player, "noWiresInBlock");
			return;
		}

		int cancelledSize = 0;
		for (UUID uuid : toRemove) {
			Wire wire = LeadWires.getApi().getWire(uuid).orElse(null);
			LeadRemoveEvent event = new LeadRemoveEvent(player, wire);
			Bukkit.getPluginManager().callEvent(event);

			if(!event.isCancelled()) {
				LeadWires.getApi().removeWire(uuid);
			} else {
				cancelledSize++;
			}
		}
		if(cancelledSize != toRemove.size()) {
			 LeadWires.sendMessage(player, "wiresRemovedFromBlock", toRemove.size());
		}
	}

	private static boolean isInBlock(@NonNull Location loc, @NonNull Vector vec) {
		return loc.getBlockX() == vec.getBlockX() && loc.getBlockY() == vec.getBlockY() && loc.getBlockZ() == vec.getBlockZ();
	}

}
