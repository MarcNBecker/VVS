package de.dhbw.vvs.model;

public class Dozent {

	private int id;
	private String titel;
	private String name;
	private String vorname;
	private String strasse;
	private String wohnort;
	private String postleitzahl;
	private String mail;
	private String telefonPrivat;
	private String telefonMobil;
	private String telefonGeschaeftlich;
	private String fax;
	private String arbeitgeber;
	private Geschlecht geschlecht;
	private Status status;
	
	public Dozent(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public String getTitel() {
		return titel;
	}

	public String getName() {
		return name;
	}

	public String getVorname() {
		return vorname;
	}

	public String getStrasse() {
		return strasse;
	}

	public String getWohnort() {
		return wohnort;
	}

	public String getPostleitzahl() {
		return postleitzahl;
	}

	public String getMail() {
		return mail;
	}

	public String getTelefonPrivat() {
		return telefonPrivat;
	}

	public String getTelefonMobil() {
		return telefonMobil;
	}

	public String getTelefonGeschaeftlich() {
		return telefonGeschaeftlich;
	}

	public String getFax() {
		return fax;
	}

	public String getArbeitgeber() {
		return arbeitgeber;
	}

	public Geschlecht getGeschlecht() {
		return geschlecht;
	}

	public Status getStatus() {
		return status;
	}

}
