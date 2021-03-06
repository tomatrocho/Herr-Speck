package com.tomatrocho.game.level.tile;

import com.tomatrocho.game.HerrSpeck;
import com.tomatrocho.game.gfx.Art;
import com.tomatrocho.game.gfx.IAbstractScreen;
import com.tomatrocho.game.level.Material;

public class StoneTile extends Tile {

    /**
     * Default constructor for the {@link StoneTile} class.
     */
    public StoneTile() {
    	this.material = Material.STONE;
    	
    	if (HerrSpeck.random.nextInt(20) == 0)
    		this.img = 3;
    	else
    		this.img = HerrSpeck.random.nextInt(3);
    }

    @Override
    public void render(IAbstractScreen screen) {
        screen.blit(Art.stoneTiles[img & 3][img / 4], x * Tile.W, y * Tile.H);
    }
}