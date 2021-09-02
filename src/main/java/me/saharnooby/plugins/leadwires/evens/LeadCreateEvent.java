package me.saharnooby.plugins.leadwires.evens;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@Setter
public class LeadCreateEvent extends LeadCancellabeEvent {

	private Location pos1;
	private Location pos2;

	public LeadCreateEvent(Player player, Location pos1, Location pos2) {
		super(player);
		this.pos1 = pos1;
		this.pos2 = pos2;
	}
}
