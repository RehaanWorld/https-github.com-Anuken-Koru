package io.anuke.koru.components;

import io.anuke.koru.network.SyncBuffer.Synced;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

@Synced
public class VelocityComponent implements Component{
	public Vector2 velocity = new Vector2();
	public transient float drag = 1f;
	public transient float limit = 2f;
	
	public VelocityComponent setDrag(float drag){
		this.drag = drag;
		return this;
	}
}
