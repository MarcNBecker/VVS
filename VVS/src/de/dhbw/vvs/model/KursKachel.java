package de.dhbw.vvs.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.dhbw.vvs.application.WebServiceException;

@SuppressWarnings("unused")
public class KursKachel {

	private Kurs kurs;
	private Blocklage planBlocklage;
	private int geplanteVorlesungen;
	private double geplanteStunden;
	private int gesamteStunden;
	private int fehlendeDozenten;
	private int fehlendeKlausuren;
	private int anzahlKonflikte;
	
	public static ArrayList<KursKachel> enrich(ArrayList<Kurs> kursList) throws WebServiceException {
		ArrayList<KursKachel> dashboard = new ArrayList<KursKachel>();
		Date heute = Calendar.getInstance().getTime();
		for(Kurs k : kursList) {
			ArrayList<Blocklage> blocklagen = Blocklage.getAll(k);
			Blocklage planBlocklage = null;
			for(Blocklage b : blocklagen) {
				if(b.getStartDatum() == null || b.getEndDatum() == null) {
					continue;
				}
				if(b.getStartDatum().compareTo(heute) <= 0 && b.getEndDatum().compareTo(heute) >= 0) {
					planBlocklage = b;
					break;
				}
			}
			if(planBlocklage == null) {
				int minSemester = 7; //lowest non valid positive semester number (except of 0)
				for(Blocklage b : blocklagen) {
					if(b.getStartDatum() == null || b.getEndDatum() == null) {
						continue;
					}
					if(b.getStartDatum().after(heute) && b.getSemester() < minSemester)  {
						minSemester = b.getSemester();
						planBlocklage = b;
					}
				}
			}
			if(planBlocklage == null) {
				continue; //history Kurs
			}
			ArrayList<VorlesungsKachel> vorlesungsKachelList = VorlesungsKachel.enrich(Vorlesung.getAll(k, planBlocklage.getSemester()));
			int geplanteVorlesungen = 0;
			double geplanteStunden = 0;
			int gesamteStunden = 0;
			int fehlendeDozenten = 0;
			int fehlendeKlausuren = 0;
			int anzahlKonflikte = 0;
			for(VorlesungsKachel vk : vorlesungsKachelList) {
				geplanteVorlesungen++;
				geplanteStunden += vk.getGeplanteStunden();
				gesamteStunden += vk.getVorlesung().getFachInstanz().getStunden();
				fehlendeDozenten += (vk.getVorlesung().getDozentID() > 0 ? 0 : 1);
				fehlendeKlausuren += (vk.getGeplanteKlausur() || vk.getVorlesung().getKeineKlausur() ? 0 : 1);
				anzahlKonflikte += vk.getAnzahlKonflikte();
			}
			KursKachel kachel = new KursKachel();
			kachel.kurs = k;
			kachel.planBlocklage = planBlocklage;
			kachel.geplanteVorlesungen = geplanteVorlesungen;
			kachel.geplanteStunden = geplanteStunden;
			kachel.gesamteStunden = gesamteStunden;
			kachel.fehlendeDozenten = fehlendeDozenten;
			kachel.fehlendeKlausuren = fehlendeKlausuren;
			kachel.anzahlKonflikte = anzahlKonflikte;
			dashboard.add(kachel);
		}
		return dashboard;
	}
	
}
