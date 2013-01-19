package com.torvergata.mytotem;

public class VoceMenu {
	private String nome;
	private String descrizione;
	private int img;
	
	public VoceMenu(String nome, String descrizione, int img) {
		super();
		this.nome = nome;
		this.descrizione = descrizione;
		this.img = img;
	}
	public String getName() {
		return nome;
	}
	public String getSurname() {
		return descrizione;
	}
	public int getImg() {
		return img;
	}
}
