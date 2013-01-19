package com.torvergata.mytotem;

public class Person {
	private String name;
	private String surname;
	private int photoRes;
	public Person(String name, String surname, int photoRes) {
		super();
		this.name = name;
		this.surname = surname;
		this.photoRes = photoRes;
	}
	public String getName() {
		return name;
	}
	public String getSurname() {
		return surname;
	}
	public int getPhotoRes() {
		return photoRes;
	}
}
