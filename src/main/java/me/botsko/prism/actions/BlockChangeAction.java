package me.botsko.prism.actions;

import me.botsko.prism.actionlibs.ActionType;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.appliers.ChangeResult;
import me.botsko.prism.appliers.ChangeResultType;
import me.botsko.prism.appliers.PrismProcessType;
import me.botsko.prism.commandlibs.Flag;
import me.botsko.prism.utils.BlockUtils;

import me.botsko.prism.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class BlockChangeAction extends BlockAction {

	@Override
	public String getNiceName() {
		String name = "";
		if (this.getActionType().getName().equals("block-fade")) {
			name += ItemUtils.getNiceName(getOldMaterial());
		}
		else {
			name += ItemUtils.getNiceName(getMaterial());
		}
		return name;
	}

	@Override
	public ChangeResult applyRollback(Player player, QueryParameters parameters, boolean is_preview) {
		final Block block = getWorld().getBlockAt(getLocation());
		return placeBlock(player, parameters, is_preview, getActionType(), getOldMaterial(), getOldBlockData(),
				getMaterial(), getBlockData(), block, false);
	}

	@Override
	public ChangeResult applyRestore(Player player, QueryParameters parameters, boolean is_preview) {
		final Block block = getWorld().getBlockAt(getLocation());
		return placeBlock(player, parameters, is_preview, getActionType(), getOldMaterial(), getOldBlockData(),
				getMaterial(), getBlockData(), block, false);
	}

	@Override
	public ChangeResult applyUndo(Player player, QueryParameters parameters, boolean is_preview) {
		final Block block = getWorld().getBlockAt(getLocation());
		return placeBlock(player, parameters, is_preview, getActionType(), getOldMaterial(), getOldBlockData(),
				getMaterial(), getBlockData(), block, false);
	}

	@Override
	public ChangeResult applyDeferred(Player player, QueryParameters parameters, boolean is_preview) {
		final Block block = getWorld().getBlockAt(getLocation());
		return placeBlock(player, parameters, is_preview, getActionType(), getOldMaterial(), getOldBlockData(),
				getMaterial(), getBlockData(), block, true);
	}

	protected ChangeResult placeBlock(Player player, QueryParameters parameters, boolean is_preview, ActionType type,
									  Material old_mat, BlockData old_data, Material new_mat, BlockData new_data, Block block,
									  boolean is_deferred) {

		final BlockAction b = new BlockAction();
		b.setActionType(type);
		b.setLocation(getLocation());
		if (parameters.getProcessType().equals(PrismProcessType.ROLLBACK)) {
			// Run verification for no-overwrite. Only reverse a change
			// if the opposite state is what's present now.
			// We skip this check because if we're in preview mode the block may
			// not
			// have been properly changed yet.
			// https://snowy-evening.com/botsko/prism/302/
			// and https://snowy-evening.com/botsko/prism/258/
			if (BlockUtils.isAcceptableForBlockPlace(block.getType())
					|| BlockUtils.areBlockIdsSameCoreItem(block.getType(), new_mat) || is_preview
					|| parameters.hasFlag(Flag.OVERWRITE)) {
				b.setMaterial(old_mat);
				b.setBlockData(old_data);
				return b.placeBlock(player, parameters, is_preview, block, is_deferred);
			}
			else {
				// System.out.print("Block change skipped because new id doesn't match what's
				// there now. There now: "
				// + block.getTypeId() + " vs " + new_id);
				return new ChangeResult(ChangeResultType.SKIPPED, null);
			}
		}
		else if (parameters.getProcessType().equals(PrismProcessType.RESTORE)) {
			// Run verification for no-overwrite. Only reapply a change
			// if the opposite state is what's present now.
			// We skip this check because if we're in preview mode the block may
			// not
			// have been properly changed yet.
			// https://snowy-evening.com/botsko/prism/302/
			// and https://snowy-evening.com/botsko/prism/258/
			if (BlockUtils.isAcceptableForBlockPlace(block.getType())
					|| BlockUtils.areBlockIdsSameCoreItem(block.getType(), old_mat) || is_preview
					|| parameters.hasFlag(Flag.OVERWRITE)) {
				b.setMaterial(new_mat);
				b.setBlockData(new_data);
				return b.placeBlock(player, parameters, is_preview, block, is_deferred);
			}
			else {
				// System.out.print("Block change skipped because old id doesn't match what's
				// there now. There now: "
				// + block.getTypeId() + " vs " + old_id);
				return new ChangeResult(ChangeResultType.SKIPPED, null);
			}
		}
		if (parameters.getProcessType().equals(PrismProcessType.UNDO)) {
			b.setMaterial(old_mat);
			b.setBlockData(old_data);
			return b.placeBlock(player, parameters, is_preview, block, is_deferred);
		}
		return new ChangeResult(ChangeResultType.SKIPPED, null);
	}
}