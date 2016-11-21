package io.anuke.koru.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;

import io.anuke.koru.Koru;
import io.anuke.koru.components.InventoryComponent;
import io.anuke.koru.items.ItemStack;
import io.anuke.koru.modules.ClientData;
import io.anuke.koru.modules.Network;
import io.anuke.koru.network.packets.InventoryClickPacket;
import io.anuke.koru.utils.Resources;

public class InventoryWidget extends VisTable{
	final int slotsize = 50;
	private ItemStack[][] stacks;
	private InventoryComponent inventory;

	public InventoryWidget() {
		inventory = Koru.module(ClientData.class).player.getComponent(InventoryComponent.class);
		stacks = inventory.inventory;
		setup();
	}

	void setup(){
		padRight(3f);
		padBottom(3f);

		for(int y = 0; y < stacks[0].length; y++){
			for(int x = 0; x < stacks.length; x++){
				Slot slot = new Slot(x, y);
				slot.addListener(new ClickListener(){
					public void clicked(InputEvent event, float x, float y){
						//inventory.clickSlot(slot.x, slot.y);
						InventoryClickPacket packet = new InventoryClickPacket();
						packet.x = slot.x;
						packet.y = slot.y;
						Koru.module(Network.class).client.sendTCP(packet);
					}
				});
				add(slot).size(slotsize);
			}
			row();
		}
	}

	public void draw(Batch batch, float alpha){
		super.draw(batch, alpha);

		if(inventory.selected != null)
			batch.draw(Resources.region(inventory.selected.item.name() + "item"), Gdx.input.getX() - slotsize / 2,
					(Gdx.graphics.getHeight() - Gdx.input.getY()) - slotsize / 2, slotsize, slotsize);

		Actor actor = getStage().hit(Gdx.input.getX(), Gdx.graphics.getHeight()-Gdx.input.getY(), true);

		if(actor instanceof Slot){
			Slot slot = (Slot) actor;

			if(stacks[slot.x][slot.y] != null){
				Resources.font().getData().setScale(1f);
				Resources.font().draw(batch, stacks[slot.x][slot.y].item.name(), Gdx.input.getX(), Gdx.graphics.getHeight()-Gdx.input.getY());
			}

		}
	}

	class Slot extends Actor{
		public final int x, y;
		public final ClickListener click;
		public Slot(int x, int y) {
			this.x = x;
			this.y = y;

			addListener((click = new ClickListener()));
		}

		public String toString(){
			return "Slot " + x + ", " + y;
		}

		public void draw(Batch batch, float alpha){
			batch.setColor(getColor());
			VisUI.getSkin().getPatch("button").draw(batch, getX(), getY(), getWidth(), getHeight());
			VisUI.getSkin().getPatch("border").draw(batch, getX(), getY(), getWidth(), getHeight());
			if(stacks[x][y] != null){
				batch.draw(Resources.region(stacks[x][y].item.name() + "item"), getX(), getY(), getWidth(),
						getHeight());

			}
		}
	}
}