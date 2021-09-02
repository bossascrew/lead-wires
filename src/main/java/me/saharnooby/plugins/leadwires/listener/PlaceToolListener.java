package me.saharnooby.plugins.leadwires.listener;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.Tools;
import me.saharnooby.plugins.leadwires.evens.LeadCreateEvent;
import me.saharnooby.plugins.leadwires.evens.LeadPointSetEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author saharNooby
 * @since 16:09 25.03.2020
 */
public final class PlaceToolListener implements Listener {

	private static final String PLACER_KEY = "LeadWires.placer.first";
	private static final String THICK_PLACER_KEY = "LeadWires.thickPlacer.first";

	@EventHandler
	public void onPlacerUse(PlayerInteractEvent e) {
		if (Tools.isPlaceTool(e.getItem())) {
			onPlacerUse(e, PLACER_KEY, false);
		}
	}

	@EventHandler
	public void onThickPlacerUse(PlayerInteractEvent e) {
		if (Tools.isThickPlaceTool(e.getItem())) {
			onPlacerUse(e, THICK_PLACER_KEY, true);
		}
	}

	private static void onPlacerUse(@NonNull PlayerInteractEvent e, @NonNull String metaKey, boolean isThick) {
		e.setCancelled(true);

		Player player = e.getPlayer();

		if (!player.hasPermission("leadwires.use-tools")) {
			LeadWires.sendMessage(player, "noPermissionsForTool");
			return;
		}

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();

			if (player.hasMetadata(metaKey)) {
				Block first = (Block) player.getMetadata(metaKey).get(0).value();

				if (first.equals(block)) {
					LeadWires.sendMessage(player, "blockAlreadySelected");
					return;
				}

				if (first.getWorld() != block.getWorld()) {
					LeadWires.sendMessage(player, "worldsAreDifferent");
					return;
				}

				LeadPointSetEvent pointSetEvent = new LeadPointSetEvent(player, block);
				Bukkit.getPluginManager().callEvent(pointSetEvent);
				if (!pointSetEvent.isCancelled()) {

					LeadCreateEvent event = new LeadCreateEvent(player, first.getLocation(), block.getLocation());
					Bukkit.getPluginManager().callEvent(event);

					if (!event.isCancelled()) {

						player.removeMetadata(metaKey, LeadWires.getInstance());
						player.setMetadata(metaKey, new FixedMetadataValue(LeadWires.getInstance(), block));

						placeWire(first, block, isThick);

						LeadWires.sendMessage(player, "wirePlaced");
					}
				}
			} else {

				LeadPointSetEvent event = new LeadPointSetEvent(player, block);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {

					player.setMetadata(metaKey, new FixedMetadataValue(LeadWires.getInstance(), block));
					LeadWires.sendMessage(player, "firstPointSet");
				}
			}
		} else if (e.getAction().name().startsWith("LEFT_CLICK_")) {
			if (player.hasMetadata(metaKey)) {

				LeadPointSetEvent event = new LeadPointSetEvent(player, null);
				Bukkit.getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					player.removeMetadata(metaKey, LeadWires.getInstance());

					LeadWires.sendMessage(player, "selectionReset");
				}
			} else {
				LeadWires.sendMessage(player, "noBlockSelected");
			}
		}
	}

	private static void placeWire(@NonNull Block from, @NonNull Block to, boolean isThick) {
		Location a = from.getLocation().add(0.5, 0.5, 0.5);
		Location b = to.getLocation().add(0.5, 0.5, 0.5);

		if (a.getY() > b.getY()) {
			// Swap the locations so the lead does not bend upwards.
			Location temp = a;
			a = b;
			b = temp;
		}

		if (isThick) {
			LeadWires.getApi().addThickWire(a, b);
		} else {
			LeadWires.getApi().addWire(a, b);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		player.removeMetadata(PLACER_KEY, LeadWires.getInstance());
		player.removeMetadata(THICK_PLACER_KEY, LeadWires.getInstance());
	}

}
