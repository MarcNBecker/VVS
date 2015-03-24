package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.WebServiceException;

public class VorlesungsKachel {

	private Vorlesung vorlesung;
	private double geplanteStunden;
	private boolean geplanteKlausur;
	
	public static ArrayList<VorlesungsKachel> enrich(ArrayList<Vorlesung> vorlesungList) throws WebServiceException {
		ArrayList<VorlesungsKachel> dashboard = new ArrayList<VorlesungsKachel>();
		for(Vorlesung v : vorlesungList) {
			ArrayList<Termin> terminList = Termin.getAll(v);
			boolean geplanteKlausur = false;
			double geplanteStunden = 0;
			for(Termin t : terminList) {
				if(t.getKlausur()) {
					geplanteKlausur = true;
				}
				long minutes = (t.getEndUhrzeit().getTime() - t.getStartUhrzeit().getTime()) / 60000;
				double duration = Math.round(((minutes - t.getPause()) / 45.0) * 10.0) / 10.0;
				geplanteStunden += duration;
			}
			VorlesungsKachel kachel = new VorlesungsKachel(v, geplanteStunden, geplanteKlausur);
			dashboard.add(kachel);
		}
		return dashboard;
	}
	
	public VorlesungsKachel(Vorlesung vorlesung, double geplanteStunden, boolean geplanteKlausur) {
		this.vorlesung = vorlesung;
		this.geplanteStunden = geplanteStunden;
		this.geplanteKlausur = geplanteKlausur;
	}

	public Vorlesung getVorlesung() {
		return vorlesung;
	}

	public double getGeplanteStunden() {
		return geplanteStunden;
	}

	public boolean getGeplanteKlausur() {
		return geplanteKlausur;
	}
	
}
