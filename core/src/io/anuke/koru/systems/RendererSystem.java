package io.anuke.koru.systems;

import io.anuke.koru.components.HitboxComponent;
import io.anuke.koru.components.PositionComponent;
import io.anuke.koru.components.RenderComponent;
import io.anuke.koru.entities.KoruEntity;
import io.anuke.koru.modules.Renderer;

import com.badlogic.ashley.core.Family;

public class RendererSystem extends KoruSystem{
	public static boolean renderHitboxes = false;
	Renderer renderer;

	public RendererSystem(Renderer renderer){
		super(Family.all(PositionComponent.class, RenderComponent.class)/*.exclude(ProjectileComponent.class, ConnectionComponent.class)*/.get(), 10);
		this.renderer = renderer;
	}

	@Override
	protected void processEntity(KoruEntity entity, float deltaTime){
		RenderComponent render = entity.mapComponent(RenderComponent.class);
		
		render.renderer.renderInternal(entity, render);
		//entity.getType().render(entity, render);

		addFade(entity, render);

		if(renderHitboxes) renderHitboxes(entity, render);
	}

	void addFade(KoruEntity entity, RenderComponent render){
		//FadeComponent fade = entity.mapComponent(FadeComponent.class);
		//for(Layer layer : render.layers.values()){
		//	if(fade != null && fade.render) layer.color.a = 1f - fade.life / fade.lifetime;
		//}
	}

	public void renderHitboxes(KoruEntity entity, RenderComponent render){
		HitboxComponent hitbox = entity.mapComponent(HitboxComponent.class);
		if(hitbox != null){
			hitbox.terrainhitbox.update(entity);
		//	renderer.layer("hitbox", hitbox.terrainRect().x, hitbox.terrainRect().y).setShape(hitbox.terrainRect().width, hitbox.terrainRect().height).setLayer(999999999);

			hitbox.entityhitbox.update(entity);
		//	renderer.layer("hitbox2", hitbox.entityRect().x, hitbox.entityRect().y).setShape(hitbox.entityRect().width, hitbox.entityRect().height).setLayer(99999999);
		}
	}
}