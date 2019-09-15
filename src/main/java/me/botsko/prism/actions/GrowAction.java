package me.botsko.prism.actions;

import org.bukkit.block.BlockState;

public class GrowAction extends BlockAction {

	public void setBlock(BlockState state) {
		if (state != null) {
			setMaterial(state.getType());
			setBlockData(state.getBlockData());
			setLocation(state.getLocation());
		}
	}

}