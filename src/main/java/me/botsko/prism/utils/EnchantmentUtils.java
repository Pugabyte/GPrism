package me.botsko.prism.utils;

import org.bukkit.enchantments.Enchantment;
import java.util.TreeMap;

public class EnchantmentUtils {

	public static String getClientSideEnchantmentName(Enchantment ench, int level) {
		String ench_name = ench.getKey().getKey().replace("_", " ");

		if (level > 0)
			ench_name += " " + ((level <= 10) ? toRoman(level) : level);

		return ench_name;
	}

	// https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
	private final static TreeMap<Integer, String> map = new TreeMap<Integer, String>() {{
		put(10, "X");
		put(9, "IX");
		put(5, "V");
		put(4, "IV");
		put(1, "I");
	}};

	public static String toRoman(int number) {
		int l =  map.floorKey(number);
		if ( number == l ) {
			return map.get(number);
		}
		return map.get(l) + toRoman(number-l);
	}

}