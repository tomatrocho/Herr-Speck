package com.tomatrocho.game.entity.mob;

import com.tomatrocho.game.HerrSpeck;
import com.tomatrocho.game.entity.Mob;
import com.tomatrocho.game.gfx.Art;
import com.tomatrocho.game.gfx.Bitmap;
import com.tomatrocho.game.gfx.IAbstractBitmap;
import com.tomatrocho.game.gfx.IAbstractScreen;
import com.tomatrocho.game.gfx.Light;
import com.tomatrocho.game.input.Keys;
import com.tomatrocho.game.input.Mouse;
import com.tomatrocho.game.level.World;
import com.tomatrocho.game.level.tile.Tile;
import com.tomatrocho.game.math.BoundingBox;
import com.tomatrocho.game.math.Vec2;
import com.tomatrocho.game.weapon.Rifle;
import com.tomatrocho.game.weapon.WeaponInventory;

public class Player extends Mob {
	
	/**
	 * 
	 */
	public static final int FIRE_MOUSE_BUTTON = 1;

    /**
     *
     */
    private int walkTime = 0;

    /**
     *
     */
    private Keys keys;
    
    /**
     * 
     */
    private boolean mouseAiming = false;

    /**
     *
     */
    private Mouse mouse;
    
    /**
     * 
     */
    private WeaponInventory weaponInventory = new WeaponInventory();
    
    /**
     * 
     */
    private int weaponInventorySlot = 0;
    
    /**
     * 
     */
    private Vec2 muzzlePosition;
    
    /**
     * 
     */
    private int muzzleTicks = 0;
    
    /**
     * 
     */
    private int muzzleFrame = 0;
    
    /**
     * 
     */
    private Light light;

    
    /**
     * 
     * @param world
     * @param keys
     * @param mouse
     */
    public Player(World world, Keys keys, Mouse mouse) {
    	this(world, world.getSpawnLocation(), keys, mouse);
    	
    	this.light = new Light(this.world, this.pos.x, this.pos.y, 150, 0xff0000ff);
    }
    
    /**
     * 
     * @param world
     * @param pos
     * @param keys
     * @param mouse
     */
    public Player(World world, Vec2 pos, Keys keys, Mouse mouse) {
    	this(world, (int) pos.x, (int) pos.y, keys, mouse);
    }

    /**
     * 
     * @param x
     * @param y
     * @param keys
     * @param mouse
     */
    public Player(World world, int x, int y, Keys keys, Mouse mouse) {
    	super(world, x * Tile.W, y * Tile.H, Team.TEAM_1);
    	
    	this.maxHealth = 100;
    	this.health = maxHealth;
    	this.speed = 2;
    	
    	bbs.put("head", new BoundingBox(this, -8, -13, 8, 0));
    	bbs.put("body", new BoundingBox(this, -6, 2, 6, 10));
    	
        this.keys = keys;
        this.mouse = mouse;
        
        weaponInventory.add(new Rifle(this));
        this.weapon = weaponInventory.get(weaponInventorySlot);
    }

    /**
     *
     */
    public void tick() {
    	// aim input
    	if (!mouse.isHidden())
    		aimByMouse(mouse.getX() - HerrSpeck.W / 2, mouse.getY() - HerrSpeck.H / 2);
    	else
    		aimByKeyboard();
    	
    	moving = false;
    	
    	// movement
        double xa = 0, ya = 0;
        if (keys.up.down()) {
            ya--;
        }
        if (keys.down.down()) {
            ya++;
        }
        if (keys.left.down()) {
            xa--;
        }
        if (keys.right.down()) {
            xa++;
        }
        
        // update orientation for rendering
        if (!mouseAiming && xa * xa + ya * ya != 0) {
            aimVector.set(xa, ya);
            aimVector.normalize();
            updateFacing();
        }
        
        // shooting
        double xaShot = 0, yaShot = 0;
        if (keys.fireUp.down()) {
        	yaShot--;
        }
        if (keys.fireDown.down()) {
        	yaShot++;
        }
        if (keys.fireLeft.down()) {
        	xaShot--;
        }
        if (keys.fireRight.down()) {
        	xaShot++;
        }
        
        // update orientation for rendering
        if (!mouseAiming && fireKeyDown() && xaShot * xaShot + yaShot * yaShot != 0) {
        	aimVector.set(xaShot, yaShot);
        	aimVector.normalize();
        	
        	updateFacing();
        }
        
        // handle movement
        if (xa != 0 || ya != 0) {
            handleMovement(xa, ya);
            moving = true;
        }
        
        handleShooting(xa, ya);
        
        // muzzle
        muzzleFrame = (muzzleFrame + 1) & 1;
        if (muzzleTicks > 0) {
        	muzzleTicks--;
        }
        
        move(xd, yd);
        xd *= 0.1;
        yd *= 0.1;
        
        // light update
        
        light.x = pos.x;
        light.y = pos.y;
        //world.reveal(World.getTileFromPosition(new Vec2(x, y)), 10);
    }
    
    /**
     * 
     * @param x
     * @param y
     */
    protected void aimByMouse(int x, int y) {
    	if (x != 0 || y != 0) {
    		mouseAiming = true;
    		
    		aimVector.set(x, y);
        	aimVector.normalize();
        	
        	updateFacing();
    	}
    }
    
    /**
     * 
     */
    protected void aimByKeyboard() {
    	mouseAiming = false;
    }
	
	/**
    *
    */
   protected void handleMovement(double xa, double ya) {
	   // facing
       int diffOrient = facing - ((int) (-Math.atan2(xa, ya) * 8 / (2 * Math.PI) + 8.5) & 7);
       if (diffOrient >= 4) {
           diffOrient -= 8;
       }
       if (diffOrient < -4) {
           diffOrient += 8;
       }
       
       // walk time
       if (diffOrient > 2 || diffOrient < -4) {
           walkTime--;
       }
       else {
           walkTime++;
       }
       
       // computing diagonal speed
       final double speed = this.speed / Math.sqrt(xa * xa + ya * ya);
       
       xa *= speed;
       ya *= speed;
       
       xd += xa;
       yd += ya;
//       pos.x += xa;
//       pos.y += ya;
   }
   
   /**
    * 
    * @param xDir
    * @param yDir
    * @param strength
    */
   public void applyImpulse(double xDir, double yDir, double strength) {
	   xd -= xDir * strength;
	   yd -= yDir * strength;
   }
   
   /**
    * 
    * @param xa
    * @param ya
    */
   protected void handleShooting(double xa, double ya) {
	   weapon.tick();
	   
	   if (fireKeyDown() || mouse.isDown(FIRE_MOUSE_BUTTON)) {
		   if (weapon instanceof Rifle) {
			   ((Rifle) weapon).fire();
		   }
	   }
   }

   /**
    * 
    * @return
    */
   private boolean fireKeyDown() {
	   return keys.fireDown.down() || keys.fireUp.down() || keys.fireDown.down() || keys.fireLeft.down() || keys.fireRight.down();
   }
   
    @Override
    public void render(IAbstractScreen screen) {
    	super.render(screen);
    	
    	// sprite blit
    	IAbstractBitmap sprite = getSprite();
        screen.blit(sprite, pos.x - sprite.getW() / 2, pos.y - sprite.getH() / 2);
        
        // muzzle
        if (muzzleTicks > 0 && facing != 4) {
        	sprite = Art.muzzle[muzzleFrame][0];
        	screen.blit(sprite, muzzlePosition.x, muzzlePosition.y);
        }
        
        // light
        light.render(HerrSpeck.getLightScreen());
    }

    @Override
    public IAbstractBitmap getSprite() {
    	IAbstractBitmap sprite = null;
    	if (moving) {
    		sprite = (Bitmap) Art.player[(walkTime / 4 % 6 + 6) % 6][facing];
    	}
    	else {
    		sprite = (Bitmap) Art.player[0][facing];
    	}
    	
    	return sprite;
    }
    
    /**
     * Updates player orientation for rendering.
     */
    private void updateFacing() {
        facing = (int) (-Math.atan2(aimVector.x, aimVector.y) * 8 / (2 * Math.PI) + 8.5) & 7;
    }
    
    /**
     * 
     * @param position
     */
    public void setMuzzlePosition(Vec2 position) {
    	this.muzzlePosition = position;
    }
    
    /**
     * 
     * @param ticks
     */
    public void setMuzzleTicks(int ticks) {
    	this.muzzleTicks = ticks;
    }
    
    /**
     * 
     * @return
     */
    public Vec2 getBarrelOffsets() {
    	Vec2 offsets = new Vec2();
    	offsets.x = -13.5 * Math.sin(facing * Math.PI / 4);
    	offsets.y = Math.sin(facing * Math.PI / 4) + 11 * Math.cos(facing * Math.PI / 4) + 8;
    	
    	return offsets;
    }
}