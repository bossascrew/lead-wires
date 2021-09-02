package me.saharnooby.plugins.leadwires.evens;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Getter
@Setter
public class LeadPointSetEvent extends LeadCancellabeEvent {

	private Block block;

	public LeadPointSetEvent(Player player, Block block) {
		super(player);
		this.block = block;
	}
}
