package com.tomatrocho.game.entity;

import com.tomatrocho.game.gfx.Bitmap;
import com.tomatrocho.game.gfx.IAbstractScreen;
import com.tomatrocho.game.gfx.Screen;
import com.tomatrocho.game.level.World;
import com.tomatrocho.game.level.tile.Tile;
import com.tomatrocho.game.math.BoundingBox;
import com.tomatrocho.game.math.IBoundingBoxOwner;

public class Light {
	
	/**
	 * 
	 */
	public static final int ambient = 0xff00000f;
	
	/**
	 * 
	 */
	private World world;
	
	/**
	 * 
	 */
	public double x;
	
	/**
	 * 
	 */
	public double y;
	
	/**
	 * 
	 */
	private int radius;
	
	/**
	 * 
	 */
	private int color;
	
	/**
	 * 
	 */
	private int[] pixels;
	
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 * @param color
	 */
	public Light(World world, double x, double y, int radius, int color) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
		
		this.pixels = new int[radius * 2 * radius * 2 + 2];
		for (int j = 0; j <= 2 * radius; j++) {
			for (int i = 0; i <= 2 * radius; i++) {
				final double distance = Math.sqrt(Math.pow(i - radius, 2) + Math.pow(j - radius, 2));
				
				if (distance < radius)
					pixels[j * radius * 2 + 1 + i] = Bitmap.getAlphaColor(color, (int) (255 - (distance / radius) * 255));
			}
		}
	}

	/**
	 * 
	 * @param screen
	 */
	public void render(IAbstractScreen screen) {
		Bitmap bitmap = new Bitmap(radius * 2, radius * 2, pixels);
		screen.blit(bitmap, x, y);
	}
}