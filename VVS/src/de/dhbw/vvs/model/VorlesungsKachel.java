package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.WebServiceException;

public class VorlesungsKachel {

	private Vorlesung vorlesung;
	private double geplanteStunden;
	private boolean geplanteKlausur;
	private int anzahlKonflikte;
	
	public static ArrayList<VorlesungsKachel> enrich(ArrayList<Vorlesung> vorlesungList) throws WebServiceException {
		ArrayList<VorlesungsKachel> dashboard = new ArrayList<VorlesungsKachel>();
		for(Vorlesung v : vorlesungList) {
			ArrayList<Termin> terminList = Termin.getAll(v);
			Kurs kurs = new Kurs(v.getKursID());
			Dozent dozent = v.getDozentID() != 0 ? new Dozent(v.getDozentID()) : null;
			boolean geplanteKlausur = false;
			double geplanteStunden = 0;
			int anzahlKonflikte = 0;
			for(Termin t : terminList) {
				if(t.getKlausur()) {
					geplanteKlausur = true;
				}
				long minutes = (t.getEndUhrzeit().getTime() - t.getStartUhrzeit().getTime()) / 60000;
				double duration = Math.round(((minutes - t.getPause()) / 45.0) * 10.0) / 10.0;
				geplanteStunden += duration;
				anzahlKonflikte += (t.hasConflicts(kurs, dozent) ? 1 : 0);
			}
			VorlesungsKachel kachel = new VorlesungsKachel();
			kachel.vorlesung = v;
			kachel.geplanteStunden = geplanteStunden;
			kachel.geplanteKlausur = geplanteKlausur;
			kachel.anzahlKonflikte = anzahlKonflikte;
			dashboard.add(kachel);
		}
		return dashboard;
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
	
	public int getAnzahlKonflikte() {
		return anzahlKonflikte;
	}
	
}
