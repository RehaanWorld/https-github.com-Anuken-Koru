package io.anuke.koru.systems;


import io.anuke.koru.components.DestroyOnTerrainHitComponent;
import io.anuke.koru.components.HitboxComponent;
import io.anuke.koru.components.PositionComponent;
import io.anuke.koru.entities.KoruEntity;
import io.anuke.koru.listeners.CollisionHandler;
import io.anuke.koru.network.IServer;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.World;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

public class CollisionSystem extends KoruSystem{
	private CollisionHandler handler;
	ObjectSet<Long> iterated = new ObjectSet<Long>();
	long lastFrameID;
	private Rectangle rect = new Rectangle();
	private GridPoint2 point = new GridPoint2();
	private Vector2 vector = new Vector2();

	public CollisionSystem(){
		super(Family.all(HitboxComponent.class, PositionComponent.class).get());
		handler = new CollisionHandler();
	}

	@Override
	void processEntity(KoruEntity entity, float delta){
		checkFrame();

		HitboxComponent hitbox = entity.mapComponent(HitboxComponent.class);
		hitbox.entityhitbox.update(entity);

		checkCollisions(entity, hitbox);
		
		GridPoint2 point = vectorTerrainCollisions(World.instance(), entity, 0, 0);
		if(point != null){ //AHH ENTITY STUCK IN BLOCK!!
			float blockx = World.world(point.x), blocky = World.world(point.y);
			vector.set(entity.getX() - blockx, entity.getY() - blocky).setLength(1f);
			entity.position().add(vector.x, vector.y);
		}
	}

	void blockCollisionEvent(KoruEntity entity){
		HitboxComponent hitbox = entity.mapComponent(HitboxComponent.class);
		if(hitbox.collideterrain && entity.mapComponent(DestroyOnTerrainHitComponent.class) != null){
			entity.removeSelfServer();
		}
	}

	void checkCollisions(KoruEntity entity, HitboxComponent hitbox){
		ImmutableArray<Entity> otherEntities = getEngine().getEntitiesFor(this.getFamily());
		for(Entity otherentity : otherEntities){
			KoruEntity other = (KoruEntity)otherentity;
			if(other == entity || iterated.contains(other.getID())) continue;
			HitboxComponent otherhitbox = other.mapComponent(HitboxComponent.class);
			otherhitbox.entityhitbox.update(other);
			if(hitbox.entityhitbox.collides(otherhitbox.entityhitbox) && otherhitbox.entityhitbox.collides(hitbox.entityhitbox) 
					&& entity.getType().collide(entity, other) && other.getType().collide(other, entity)){
				collisionEvent(entity,other);
			//	iterated.add(other.getID());
			}
		
		}
		iterated.add(entity.getID());
	}
	
	void collisionEvent(KoruEntity entitya, KoruEntity entityb){
		handler.dispatchEvent(entitya, entityb);
	}


	public void moveWithCollisions(World world, KoruEntity entity, float mx, float my){
		boolean wallcollision = false;
		if( !checkTerrainCollisions(world, entity, mx, 0)){
			entity.position().add(mx, 0);
		}else{
			blockCollisionEvent(entity);
			wallcollision = true;
		}

		if( !checkTerrainCollisions(world, entity, 0, my)){
			entity.position().add(0, my);
		}else{
			if( !wallcollision) blockCollisionEvent(entity);
		}
	}

	public GridPoint2 vectorTerrainCollisions(World world, KoruEntity entity, float mx, float my){
		
		HitboxComponent component = entity.mapComponent(HitboxComponent.class);
		float newx = entity.getX() + mx;
		float newy = entity.getY() + my;
		component.terrainhitbox.update(entity, mx, my);
		int tilex = World.tile(newx);
		int tiley = World.tile(newy);
		for(int rx = -1;rx <= 1;rx ++){
			for(int ry = -1;ry <= 1;ry ++){
				int worldx = tilex + rx, worldy = tiley + ry;
				if(!world.inBounds(worldx, worldy)) continue;
				Tile tile = world.tile(worldx, worldy);
				if( !tile.solid()) continue;
				if(component.terrainhitbox.collides(tile.solidMaterial().getType().getRect(worldx, worldy, rect))){
					point.set(worldx, worldy);
					return point;
				}
				//	rect.set(0,0,0,0);
			}
		}
		return null;
	}
	
	public boolean checkTerrainCollisions(World world, KoruEntity entity, float mx, float my){
		return vectorTerrainCollisions(world, entity, mx, my) != null;
	}

	private void checkFrame(){
		if(IServer.instance().getFrameID() != lastFrameID){
			iterated.clear();
			lastFrameID = IServer.instance().getFrameID();
		}
	}

}