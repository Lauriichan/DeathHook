package org.playuniverse.minecraft.deathhook;

import java.awt.Color;

public final class ColorHelper {

	public static final String HEX_FORMAT = "%02X";

	private ColorHelper() {
	}

	public static String toHexColor(Color color) {
		StringBuilder builder = new StringBuilder("#");
		builder.append(String.format(HEX_FORMAT, color.getRed()));
		builder.append(String.format(HEX_FORMAT, color.getGreen()));
		builder.append(String.format(HEX_FORMAT, color.getBlue()));
		return builder.toString();
	}

	public static Color fromHexColor(String color) {
		if (color.startsWith("#")) {
			color = color.replace("#", "");
		}
		if (color.length() < 6) {
			return Color.BLACK;
		}
		int red = Integer.parseInt(color.substring(0, 2), 16);
		int green = Integer.parseInt(color.substring(2, 4), 16);
		int blue = Integer.parseInt(color.substring(4, 6), 16);
		return new Color(red, green, blue);
	}

}
