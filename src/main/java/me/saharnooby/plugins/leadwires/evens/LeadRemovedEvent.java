package me.saharnooby.plugins.leadwires.evens;

import lombok.Getter;
import me.saharnooby.plugins.leadwires.wire.Wire;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@Getter
public class LeadRemovedEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	private final UUID wireUuid;

	public LeadRemovedEvent(UUID wireUuid) {
		this.wireUuid = wireUuid;
	}
}
