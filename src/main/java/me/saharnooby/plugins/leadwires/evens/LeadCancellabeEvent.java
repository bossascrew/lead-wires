package me.saharnooby.plugins.leadwires.evens;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter
@Setter
public class LeadCancellabeEvent extends LeadEvent implements Cancellable {

	private boolean cancelled;

	public LeadCancellabeEvent(Player player) {
		super(player);
	}

}
