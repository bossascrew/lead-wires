package me.saharnooby.plugins.leadwires.evens;

import lombok.Getter;
import lombok.Setter;
import me.saharnooby.plugins.leadwires.wire.Wire;
import org.bukkit.entity.Player;

@Getter
@Setter
public class LeadRemoveEvent extends LeadCancellabeEvent {

	public final Wire wire;

	public LeadRemoveEvent(Player player, Wire wire) {
		super(player);
		this.wire = wire;
	}
}
