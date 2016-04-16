package net.pixelstatic.koru.world;

public class Tile{
	public Material tile = Material.air;
	public Material block = Material.air;
	
	public Tile setTileMaterial(Material m){
		this.tile = m;
		return this;
	}
	
	public Tile setBlockMaterial(Material m){
		this.block = m;
		return this;
	}
	
	public String toString(){
		return "Tile:[block="+block+" tile="+tile+ "]";
	}
}