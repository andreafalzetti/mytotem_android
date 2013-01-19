package com.torvergata.mytotem;
 
public class RigaDoppia {
	private String nome;
    private String descrizione;
    
    public RigaDoppia(String nome, String descrizione)
    {
    	super();
    	this.nome = nome;
    	this.descrizione = descrizione;
    }
        public String getNome() {
                return nome;
        }
        public String getDescrizione() {
                return descrizione;
        }
}