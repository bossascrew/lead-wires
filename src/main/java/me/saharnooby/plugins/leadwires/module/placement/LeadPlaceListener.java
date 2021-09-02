package me.saharnooby.plugins.leadwires.module.placement;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.plugins.leadwires.LeadWires;
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

import java.util.UUID;

/**
 * @author saharNooby
 * @since 10:30 30.03.2020
 */
@RequiredArgsConstructor
final class LeadPlaceListener implements Listener {

	private final LeadPlacementModule module;

	private static final String KEY = "LeadWires.leadPlacement.first";

	@EventHandler
	public void onPlayerRightClickBlock(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		if (e.getItem() == null || !Util.isLead(e.getItem().getType())) {
			return;
		}

		ModuleConfig config = this.module.getConfig();

		if (!config.getAllowedBlocks().contains(e.getClickedBlock().getType().name())) {
			return;
		}

		e.setCancelled(true);

		Player player = e.getPlayer();

		if (!config.getPermission().isEmpty() && !player.hasPermission(config.getPermission())) {
			this.module.sendMessage(player, "noPermission");
			return;
		}

		Block block = e.getClickedBlock();

		if (!player.hasMetadata(KEY)) {

			LeadPointSetEvent event = new LeadPointSetEvent(player, block);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {

				setFirstBlock(player, block);
				this.module.sendMessage(player, "firstPointSet");
				return;
			}
		}

		Block first = (Block) player.getMetadata(KEY).get(0).value();

		if (first.equals(block)) {
			// Clicking on the same block.
			return;
		}

		LeadPointSetEvent event = new LeadPointSetEvent(player, block);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		if (!config.getAllowedBlocks().contains(first.getType().name())) {
			// First block was changed.

			setFirstBlock(player, block);
			this.module.sendMessage(player, "firstPointSet");
			return;
		}

		if (first.getWorld() != block.getWorld()) {
			resetFirstBlock(player);
			return;
		}

		if (first.getLocation().distance(block.getLocation()) > config.getMaxLength()) {
			this.module.sendMessage(player, "tooLong", config.getMaxLength());
			return;
		}

		LeadCreateEvent createEvent = new LeadCreateEvent(player, first.getLocation(), block.getLocation());
		Bukkit.getPluginManager().callEvent(createEvent);

		if (event.isCancelled()) {
			return;
		}

		placeWire(first, block);

		resetFirstBlock(player);

		this.module.sendMessage(player, "wirePlaced");

		Util.takeFromHand(e);

		if (config.isContinueFromLastPoint()) {
			setFirstBlock(player, block);
		}
	}

	@EventHandler
	public void onPlayerLeftClick(PlayerInteractEvent e) {
		if (!e.getAction().name().startsWith("LEFT_CLICK_")) {
			return;
		}

		if (e.getItem() == null || !Util.isLead(e.getItem().getType())) {
			return;
		}

		Player player = e.getPlayer();

		if (!player.hasMetadata(KEY)) {
			return;
		}

		e.setCancelled(true);

		LeadPointSetEvent event = new LeadPointSetEvent(player, null);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			resetFirstBlock(player);

			this.module.sendMessage(player, "firstPointReset");
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		resetFirstBlock(e.getPlayer());
	}

	private void placeWire(@NonNull Block from, @NonNull Block to) {
		Location a = from.getLocation().add(0.5, 0.5, 0.5);
		Location b = to.getLocation().add(0.5, 0.5, 0.5);

		if (a.getY() > b.getY()) {
			// Swap the locations so the lead does not bend upwards.
			Location temp = a;
			a = b;
			b = temp;
		}

		UUID uuid = LeadWires.getApi().addWire(a, b);

		this.module.getStorage().addLead(LeadWires.getApi().getWire(uuid).orElseThrow(RuntimeException::new));
	}

	private void setFirstBlock(@NonNull Player player, @NonNull Block block) {

		resetFirstBlock(player);
		player.setMetadata(KEY, new FixedMetadataValue(LeadWires.getInstance(), block));
	}

	private void resetFirstBlock(@NonNull Player player) {
		player.removeMetadata(KEY, LeadWires.getInstance());
	}
}
