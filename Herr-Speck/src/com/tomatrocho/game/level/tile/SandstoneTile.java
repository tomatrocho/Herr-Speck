package com.tomatrocho.game.level.tile;

import com.tomatrocho.game.gfx.Art;
import com.tomatrocho.game.gfx.IAbstractScreen;
import com.tomatrocho.game.level.Material;

public class SandstoneTile extends Tile {

    /**
     * Default constructor for the {@link SandstoneTile} class.
     */
    public SandstoneTile() {
    	this.material = Material.SANDSTONE;
    	this.img = Tile.PLAIN_IMG;
    	this.connectableWith.add(Material.WATER);
    }

    @Override
    public void render(IAbstractScreen screen) {
        screen.blit(Art.sandstoneTiles[img & 7][img / 8], x * Tile.W, y * Tile.H);
    }
    
    @Override
    public boolean isConnectable() {
    	return true;
    }
}