package me.botsko.prism.actions;

import org.bukkit.Material;

import java.util.EnumMap;

public class UseAction extends GenericAction {

	private static final EnumMap<Material, String> names = new EnumMap<>(Material.class);

	static {
		names.put(Material.FLINT_AND_STEEL, "tnt");
	}

	@Override
	public String getNiceName() {
		Material material = getMaterial();
		String customName = names.get(material);

		if(customName == null) {
			return material.name().toLowerCase();
		}

		return customName;
	}

}