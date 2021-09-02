package me.saharnooby.plugins.leadwires.evens;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class LeadEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	private final Player player;

	public LeadEvent(Player player) {
		this.player = player;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
