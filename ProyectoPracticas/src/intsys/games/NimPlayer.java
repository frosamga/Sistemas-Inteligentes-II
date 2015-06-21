package intsys.games;

import java.awt.Color;

public enum NimPlayer {
	PLAYER_1 ("Red", Color.RED),
	PLAYER_2 ("Green", Color.GREEN);

	private String name;
	private final Color color;
	
	NimPlayer(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	
	public Color getColor() { return color; }
	public String toString() { return name; }

}
