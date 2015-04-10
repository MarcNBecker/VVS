"use strict";
var model = new function() {
	/*
	 * Helper functions
	 */
	this.helper = new function() {
		this.ModulplanComplete = function() {
			this.id = 0;
			this.studiengang = "";
			this.vertiefungsrichtung = "";
			this.gueltigAb = 0;
			this.modulInstanzList = [];
		};
		
		this.getModulplanComplete = function(m, c, e) {
			//Read basic modulplan information
			model.webService.getModulplan(m, function(api1) {
				//Modulplan exists
				if(!api1.isError) {
					var modulplan = new model.helper.ModulplanComplete();
					modulplan.id = api1.response.id;
					modulplan.studiengang = api1.response.studiengang;
					modulplan.vertiefungsrichtung = api1.response.vertiefungsrichtung;
					modulplan.gueltigAb = api1.response.gueltigAb;
					//Read ModulInstanzList
					model.webService.getAllModulInstanzen(m, function(api2) {
						//ModulInstanzList can be read
						if(!api2.isError) {
							//List is empty return Modulplan
							if(api2.response.length === 0) {
								c(modulplan);
								return;
							}
							var deepRuns = 0;
							var fachInstanzError = false;
							//Parse list of ModulInstanz
							for(var i=0; i<api2.response.length; i++) {
								var currentModulInstanz = api2.response[i];
								currentModulInstanz.fachInstanzList = [];
								//Add ModulInstanz to Modulplan
								modulplan.modulInstanzList.push(currentModulInstanz);
								//Read FachInstanzen
								model.webService.getAllFachInstanzen(currentModulInstanz, function(api3) {
									//FachInstanzList can be read
									if(!fachInstanzError) {
										if(!api3.isError) {
											for(var ii=0; ii<api3.response.length; ii++) {
												var currentFachInstanz = api3.response[ii];
												//Search correct ModulInstanz; can't rely on outer scope
												for(var iii=0; iii<modulplan.modulInstanzList.length; iii++) {
													if(modulplan.modulInstanzList[iii].id === currentFachInstanz.modulInstanzID) {
														modulplan.modulInstanzList[iii].fachInstanzList.push(currentFachInstanz);			
														break;
													}
												}
											}
										} else {
											fachInstanzError = true;
											e(modulplan); //Reading some FachInstanzList failed, return Modulplan, but to error callback
										}
										//Every ModulInstanz triggers a FachInstanzList call (deepRun)
										deepRuns++;
										//Last call is triggered, then go to callback
										if(api2.response.length === deepRuns) {
											c(modulplan);
										}
									}
								});
							}
						} else { //Reading ModulInstanzList failed, return Modulplan, but to error callback
							e(modulplan);
						}
					});
				} else { //Reading Modulplan failed, return null
					e(null);
				}
			});
		};

		this.setModulInstanz = function(mi, c, cd) {
			if(mi.modul.id === 0) {
				model.webService.createModulInstanzWithModul(mi, c, cd);
			} else {
				model.webService.createModulInstanz(mi, c, cd);
			}
		};
		
		this.setFachInstanz = function(mi, fi, c, cd) {
			if(fi.fach.id === 0) {
				model.webService.createFachInstanzWithFach(mi, fi, c, cd);
			} else {
				model.webService.createFachInstanz(mi, fi, c, cd);
			}
		};
		
	};
	/*
	 * Template objects
	 */
	this.templates = new function() {
		this.Blocklage = function() {
			this.kursID = 0;
			this.semester = 0;
			this.startDatum = null; //yyyy-MM-dd
			this.endDatum = null; //yyyy-MM-dd
			this.raum = "";
		};
		
		this.Dozent = function () {
			this.id = 0;
			this.titel = "";
			this.name = "";
			this.vorname = "";
			this.geschlecht = 0; //GeschlechtEnum
			this.strasse = "";
			this.wohnort = "";
			this.postleitzahl = ""; //Numeric string
			this.mail = ""; //Valid mail
			this.telefonPrivat = ""; //Valid phone
			this.telefonMobil = ""; //Valid phone
			this.telefonGeschaeftlich = ""; //Valid phone
			this.fax = ""; //Valid phone
			this.arbeitgeber = "";
			this.status = 0; //StatusEnum
			this.angelegt = null; //yyyy-MM-dd
			this.geaendert = null; //yyyy-MM-dd
			this.maxFachJahr = 0; //This is only filled at times, when the dozent is loaded in context with a fach information and specifies a year
		};
		
		this.Dozent.prototype.StatusEnum = {
				neu: {
					ordinal : 0,
					string : "Neu"
				},
				aktiv: {
					ordinal: 1,
					string: "Aktiv"
				},
				inaktiv: {
					ordinal: 2,
					string: "Inaktiv"
				},
				all: ["Neu", "Aktiv", "Inaktiv"]
		};

		this.Dozent.prototype.GeschlechtEnum = {
				m: {
					ordinal : 0,
					string: "Herr"
				},
				f: {
					ordinal: 1,
					string: "Frau"
				},
				all: ["Herr", "Frau"]
		};
		
		this.Fach = function() {
			this.id = 0;
			this.name = "";
			this.kurzbeschreibung = "";
			this.maxDozentJahr = 0; //This is only filled at times, when the fach is loaded in context with a dozent information and specifies a year
		};

		this.FachInstanz = function() {
			this.id = 0;
			this.fach = new model.templates.Fach();
			this.modulInstanzID = 0;
			this.semester = ""; //number
			this.stunden = ""; //number
		};

		this.Feiertag = function() {
			this.datum = null; //yyyy-MM-dd
			this.name = "";
		};
		
		this.Kommentar = function() {
			this.id = 0;
			this.dozentID = 0;
			this.text = "";
			this.verfasser = ""; //User
			this.timestamp = null; //yyyy-MM-dd
		};

		this.Kurs = function() {
			this.id = 0;
			this.kursname = "";
			this.kursmail = ""; //Valid mail
			this.modulplanID = 0;
			this.studentenAnzahl = ""; //number
			this.kurssprecherVorname = "";
			this.kurssprecherName = "";
			this.kurssprecherMail = ""; //Valid mail
			this.kurssprecherTelefon = ""; //Valid phone
			this.studiengangsleiterID = 0;
			this.sekretariatName = "";
		};

		this.KursKachel = function() {
			this.kurs = new model.templates.Kurs();
			this.planBlocklage = new model.templates.Blocklage();
			this.geplanteVorlesungen = 0; 
			this.geplanteStunden = 0.0;
			this.gesamteStunden = 0;
			this.fehlendeDozenten = 0;
			this.fehlendeKlausuren = 0;
			this.anzahlKonflikte = 0;
		};
		
		this.Modul = function() {
			this.id = 0;
			this.name = "";
			this.kurzbeschreibung = "";
		};

		this.ModulInstanz = function() {
			this.id = 0;
			this.modul = new model.templates.Modul();
			this.modulplanID = 0;
			this.credits = ""; //number
		};

		this.Modulplan = function() {
			this.id = 0;
			this.studiengang = "";
			this.vertiefungsrichtung = "";
			this.gueltigAb = ""; //number
			this.vorlage = 0;
		};

		this.Studiengangsleiter = function() {
			this.id = 0;
			this.name = "";
		};
		
		this.Termin = function() {
			this.id = 0;
			this.vorlesungID = 0;
			this.datum = null; //yyyy-MM-dd
			this.startUhrzeit = null; //HH:MM
			this.endUhrzeit = null; //HH:MM
			this.pause = 0;
			this.raum = "";
			this.klausur = false;
		};
		
		this.User = function() {
			this.name = "";
			this.passwort = "";
			this.repraesentiert = 0; //An ID of a Studiengangsleiter
		};
		
		this.Vorlesung = function() {
			this.id = 0;
			this.kursID = 0;
			this.fachInstanz = new model.templates.FachInstanz();
			this.dozentID = 0;
			this.semester = 0;
			this.keineKlausur = false;
		};
		
		this.VorlesungsKachel = function() {
			this.vorlesung = new model.templates.Vorlesung();
			this.geplanteStunden = 0;
			this.geplanteKlausur = false;
			this.anzahlKonflikte = 0;
		};
		
	};
	
	/*
	 * WebService calls
	 */
	this.webService = new function() {

		var self = this;
		var allowedMethods = ["GET", "POST", "PUT", "DELETE"];
		var rootURI = "";
		var apiURI = "/VVS/api/v1";
		var birtURI = "/birt-viewer";
		
		var dozentenURI = "/dozenten";
		var dozentURI = "/dozenten/{dozentID}";
		var dozentFaecherURI = "/dozenten/{dozentID}/faecher";
		var dozentFachURI = "/dozenten/{dozentID}/faecher/{fachID}";
		var dozentenFachURI = "/dozenten/faecher/{fachID}";
		var dozentKommentareURI = "/dozenten/{dozentID}/kommentare";
		var dozentKommentarURI = "/dozenten/{dozentID}/kommentare/{kommentarID}";

		var studiengangsleiterDashboardURI = "/studiengangsleiter/{studiengangsleiterID}/dashboard";
		
		var kurseURI = "/kurse";
		var kursURI = "/kurse/{kursID}";
		var kursBlocklagenURI = "/kurse/{kursID}/blocklagen";
		var kursBlocklageURI = "/kurse/{kursID}/blocklagen/{semester}";
		var kursBlocklageDozentenURI = "/kurse/{kursID}/blocklagen/{semester}/dozenten";
		
		var studiengangsleiterAllURI = "/studiengangsleiter";
		var studiengangsleiterURI = "/studiengangsleiter/{studiengangsleiterID}";
		
		var userAllURI = "/user";
		var userURI = "/user/{name}";
		
		var feiertageURI = "/feiertage/{jahr}";
		var feiertagURI = "/feiertage/{jahr}/{datum}";
		
		var faecherURI = "/faecher";
		var moduleURI = "/module";
		var modulplaeneURI = "/modulplaene";
		var modulplanURI = "/modulplaene/{modulplanID}";
		var modulInstanzenURI = "/modulplaene/{modulplanID}/module";
		var modulInstanzURI = "/modulplaene/{modulplanID}/module/{modulID}";
		var fachInstanzenURI = "/modulplaene/{modulplanID}/module/{modulID}/faecher";
		var fachInstanzURI = "/modulplaene/{modulplanID}/module/{modulID}/faecher/{fachID}";
		var quickDeleteFachInstanzURI = "/modulplaene/quickdelete/faecher/{fachInstanzID}";
		
		var kursVorlesungen = "/kurse/{kursID}/vorlesungen";
		var kursVorlesungenOffen = "/kurse/{kursID}/vorlesungen/offen";
		var kursVorlesungenSondertermine = "/kurse/{kursID}/{semester}/vorlesungen/sondertermine";
		
		var vorlesungenURI = "/kurse/{kursID}/{semester}/vorlesungen";
		var vorlesungenSondertermineURI = "/kurse/{kursID}/{semester}/vorlesungen/sondertermine";
		var vorlesungenGroupEURI = "/kurse/{kursID}/{semester}/vorlesungen/groupe";
		var vorlesungenXMLURI = "/kurse/{kursID}/{semester}/vorlesungen/xml";
		var vorlesungURI = "/kurse/{kursID}/{semester}/vorlesungen/{vorlesungsID}";
		var vorlesungDozentenURI = "/kurse/{kursID}/{semester}/vorlesungen/{vorlesungsID}/dozenten";
		var vorlesungTermineURI = "/kurse/{kursID}/{semester}/vorlesungen/{vorlesungsID}/termine";
		var vorlesungTerminURI = "/kurse/{kursID}/{semester}/vorlesungen/{vorlesungsID}/termine/{terminID}";
		
		var termineKursURI = "/termine/{datum}/kurse/{kursID}";
		var termineDozentURI = "/termine/{datum}/dozenten/{dozentID}";
		var termineRaumURI = "/termine/{datum}/raeume/{raum}";
		
		var kursPlanURI = "/run?__report=Vorlesungsplan_Hochformat.rptdesign&__format=pdf&Kurs={kursID}&Semester={semester}&__locale=de";
		var kursPlanQuerURI = "/run?__report=Vorlesungsplan_Querformat.rptdesign&__format=pdf&Kurs={kursID}&Semester={semester}&__locale=de";
		var dozentenPlanURI = "/run?__report=Vorlesungsplan_Dozent.rptdesign&__format=pdf&Vorlesung={vorlesungID}&__locale=de";
		
		this.getAllDozenten = function(c) {
			self.doRequest(dozentenURI, "GET", null, c);
		};
		
		this.createDozent = function(d, c) {
			self.doRequest(dozentenURI, "POST", d, c);
		};
		
		this.getDozent = function(d, c) {
			self.doRequest(dozentURI.replace("{dozentID}", d.id), "GET", null, c);
		};
		
		this.updateDozent = function(d, c) {
			self.doRequest(dozentURI.replace("{dozentID}", d.id), "PUT", d, c);	
		};
		
		this.deleteDozent = function(d, c) {	
			self.doRequest(dozentURI.replace("{dozentID}", d.id), "DELETE", null, c);
		};
		
		this.getAllDozentFaecher = function(d, c) {
			self.doRequest(dozentFaecherURI.replace("{dozentID}", d.id), "GET", null, c);
		};
		
		this.setDozentFach = function(d, f, c) {
			self.doRequest(dozentFachURI.replace("{dozentID}", d.id).replace("{fachID}", f.id), "PUT", "{}", c);
		};
		
		this.deleteDozentFach = function(d, f, c) {
			self.doRequest(dozentFachURI.replace("{dozentID}", d.id).replace("{fachID}", f.id), "DELETE", null, c);	
		};
		
		this.getAllDozentenFach = function(f, c) {
			self.doRequest(dozentenFachURI.replace("{fachID}", f.id), "GET", null, c);
		};
		
		this.getAllDozentKommentare = function(d, c) {
			self.doRequest(dozentKommentareURI.replace("{dozentID}", d.id), "GET", null, c);
		};
		
		this.createDozentKommentar = function(k, c) {
			self.doRequest(dozentKommentareURI.replace("{dozentID}", k.dozentID), "POST", k, c);
		};
		
		this.deleteDozentKommentar = function(k, c) {
			self.doRequest(dozentKommentarURI.replace("{dozentID}", k.dozentID).replace("{kommentarID}", k.id), "DELETE", null, c);	
		};
		
		this.getStudiengangsleiterDashboard = function(s, c) {
			self.doRequest(studiengangsleiterDashboardURI.replace("{studiengangsleiterID}", s.id), "GET", null, c);
		};
		
		this.getAllKurse = function(c) {
			self.doRequest(kurseURI, "GET", null, c);
		};
		
		this.createKurs = function(k, c) {
			self.doRequest(kurseURI, "POST", k, c);
		};
		
		this.getKurs = function(k, c) {
			self.doRequest(kursURI.replace("{kursID}", k.id), "GET", null, c);
		};
		
		this.updateKurs = function(k, c) {
			self.doRequest(kursURI.replace("{kursID}", k.id), "PUT", k, c);	
		};
		
		this.deleteKurs = function(k, c) {
			self.doRequest(kursURI.replace("{kursID}", k.id), "DELETE", null, c);
		};
		
		this.getAllKursBlocklagen = function(k, c) {
			self.doRequest(kursBlocklagenURI.replace("{kursID}", k.id), "GET", null, c);
		};
		
		this.setKursBlocklage = function(b, c) {
			self.doRequest(kursBlocklageURI.replace("{kursID}", b.kursID).replace("{semester}", b.semester), "PUT", b, c);
		};
		
		this.getAllKursBlocklageDozenten = function(b, c) {
			self.doRequest(kursBlocklageDozentenURI.replace("{kursID}", b.kursID).replace("{semester}", b.semester), "GET", null, c);
		};
		
		this.getAllStudiengangsleiter = function(c) {
			self.doRequest(studiengangsleiterAllURI, "GET", null, c);
		};
		
		this.createStudiengangsleiter = function(s, c) {
			self.doRequest(studiengangsleiterAllURI, "POST", s, c);
		};
		
		this.getStudiengangsleiter = function(s, c) {
			self.doRequest(studiengangsleiterURI.replace("{studiengangsleiterID}", s.id), "GET", null, c);
		};
		
		this.updateStudiengangsleiter = function(s, c) {
			self.doRequest(studiengangsleiterURI.replace("{studiengangsleiterID}", s.id), "PUT", s, c);
		};
		
		this.deleteStudiengangsleiter = function(s, c) {
			self.doRequest(studiengangsleiterURI.replace("{studiengangsleiterID}", s.id), "DELETE", null, c);
		};
		
		this.getAllUser = function(c) {
			self.doRequest(userAllURI, "GET", null, c);
		};
		
		this.createUser = function(u, c) {
			self.doRequest(userAllURI, "POST", u, c);
		};
				
		this.getUser = function(u, c) {
			self.doRequest(userURI.replace("{name}", u.name), "GET", null, c);
		};
		
		this.authenticateUser = function(u, c) {
			self.doRequest(userURI.replace("{name}", u.name), "POST", u, c);
		};
		
		this.updateUser = function(u, c) {
			self.doRequest(userURI.replace("{name}", u.name), "PUT", u, c);	
		};
		
		this.deleteUser = function(u, c) {
			self.doRequest(userURI.replace("{name}", u.name), "DELETE", null, c);	
		};
		
		this.getAllFeiertage = function(jahr, c) {
			self.doRequest(feiertageURI.replace("{jahr}", jahr), "GET", null, c);
		};
		
		this.setFeiertag = function(f, c) {
			self.doRequest(feiertagURI.replace("{jahr}", f.datum.substring(0,4)).replace("{datum}", f.datum), "PUT", f, c);
		};
		
		this.deleteFeiertag = function(f, c) {
			self.doRequest(feiertagURI.replace("{jahr}", f.datum.substring(0,4)).replace("{datum}", f.datum), "DELETE", f, c);
		};
		
		this.getAllFaecher = function(c) {
			self.doRequest(faecherURI, "GET", null, c);
		};
		
		this.getAllModule = function(c) {
			self.doRequest(moduleURI, "GET", null, c);
		};
		
		this.getAllModulplaene = function(c) {
			self.doRequest(modulplaeneURI, "GET", null, c);
		};
		
		this.createModulplan = function(m, c) {
			self.doRequest(modulplaeneURI, "POST", m, c);
		};
		
		this.getModulplan = function(m, c) {
			self.doRequest(modulplanURI.replace("{modulplanID}", m.id), "GET", null, c);
		};
		
		this.updateModulplan = function(m, c) {
			self.doRequest(modulplanURI.replace("{modulplanID}", m.id), "PUT", m, c);
		};
		
		this.deleteModulplan = function(m, c) {
			self.doRequest(modulplanURI.replace("{modulplanID}", m.id), "DELETE", null, c);
		};
		
		this.getAllModulInstanzen = function(m, c) {
			self.doRequest(modulInstanzenURI.replace("{modulplanID}", m.id), "GET", null, c);
		};
		
		this.createModulInstanzWithModul = function(mi, c, cd) {
			self.doRequest(modulInstanzenURI.replace("{modulplanID}", mi.modulplanID), "POST", mi, c, cd);	
		};
		
		this.createModulInstanz = function(mi, c, cd) {
			self.doRequest(modulInstanzURI.replace("{modulplanID}", mi.modulplanID).replace("{modulID}", mi.modul.id), "PUT", mi, c, cd);
		};
		
		this.deleteModulInstanz = function(mi, c) {
			self.doRequest(modulInstanzURI.replace("{modulplanID}", mi.modulplanID).replace("{modulID}", mi.modul.id), "DELETE", null, c);	
		};
		
		this.getAllFachInstanzen = function(mi, c) {
			self.doRequest(fachInstanzenURI.replace("{modulplanID}", mi.modulplanID).replace("{modulID}", mi.modul.id), "GET", null, c);
		};
		
		this.createFachInstanzWithFach = function(mi, fi, c, cd) {
			self.doRequest(fachInstanzenURI.replace("{modulplanID}", mi.modulplanID).replace("{modulID}", mi.modul.id), "POST", fi, c, cd);	
		};
		
		this.createFachInstanz = function(mi, fi, c, cd) {
			self.doRequest(fachInstanzURI.replace("{modulplanID}", mi.modulplanID).replace("{modulID}", mi.modul.id).replace("{fachID}", fi.fach.id), "PUT", fi, c, cd);	
		};
		
		this.deleteFachInstanz = function(mi, fi, c) {
			self.doRequest(fachInstanzURI.replace("{modulplanID}", mi.modulplanID).replace("{modulID}", mi.modul.id).replace("{fachID}", fi.fach.id), "DELETE", null, c);	
		};
		
		this.quickDeleteFachInstanz = function(fi, c) {
			self.doRequest(quickDeleteFachInstanzURI.replace("{fachInstanzID}", fi.id), "DELETE", null, c);
		};

		this.getAllVorlesungen = function(k, c) {
			self.doRequest(kursVorlesungen.replace("{kursID}", k.id), "GET", null, c);	
		};
		
		this.getAllVorlesungenOffen = function(k, c) {
			self.doRequest(kursVorlesungenOffen.replace("{kursID}", k.id), "GET", null, c);	
		};
		
		this.getAllVorlesungenSemester = function(k, s, c) {
			self.doRequest(vorlesungenURI.replace("{kursID}", k.id).replace("{semester}", s), "GET", null, c);
		};
		
		this.getAllVorlesungenSondertermine = function(k, s, c) {
			self.doRequest(vorlesungenSondertermineURI.replace("{kursID}", k.id).replace("{semester}", s), "GET", null, c);
		};
		
		this.getGroupEURI = function (k, s) {
			return rootURI + apiURI + vorlesungenGroupEURI.replace("{kursID}", k.id).replace("{semester}", s);
		};
		
		this.getXMLURI = function(k, s) {
			return rootURI + apiURI + vorlesungenXMLURI.replace("{kursID}", k.id).replace("{semester}", s);
		};
		
		this.createVorlesung = function(v, c) {
			self.doRequest(vorlesungenURI.replace("{kursID}", v.kursID).replace("{semester}", v.semester), "POST", v, c);
		};
		
		this.getVorlesung = function(v, c) {
			self.doRequest(vorlesungURI.replace("{kursID}", v.kursID).replace("{semester}", v.semester).replace("{vorlesungsID}", v.id), "GET", null, c);
		};
		
		this.updateVorlesung = function(v, c) {
			self.doRequest(vorlesungURI.replace("{kursID}", v.kursID).replace("{semester}", v.semester).replace("{vorlesungsID}", v.id), "PUT", v, c);
		};
		
		this.deleteVorlesung = function(v, c) {
			self.doRequest(vorlesungURI.replace("{kursID}", v.kursID).replace("{semester}", v.semester).replace("{vorlesungsID}", v.id), "DELETE", null, c);
		};
		
		this.getVorlesungDozenten = function(v, c) {
			self.doRequest(vorlesungDozentenURI.replace("{kursID}", v.kursID).replace("{semester}", v.semester).replace("{vorlesungsID}", v.id), "GET", null, c);	
		};
		
		this.getAllVorlesungTermine = function(v, c) {
			self.doRequest(vorlesungTermineURI.replace("{kursID}", v.kursID).replace("{semester}", v.semester).replace("{vorlesungsID}", v.id), "GET", null, c);		
		};
		
		this.createVorlesungTermin = function(v, t, c, cd) {
			self.doRequest(vorlesungTermineURI.replace("{kursID}", v.kursID).replace("{semester}", v.semester).replace("{vorlesungsID}", v.id), "POST", t, c, cd);		
		};
		
		this.getVorlesungTermin = function(v, t, c) {
			self.doRequest(vorlesungTerminURI.replace("{kursID}", v.kursID).replace("{semester}", v.semester).replace("{vorlesungsID}", v.id).replace("{terminID}", t.id), "GET", null, c);		
		};
		
		this.updateVorlesungTermin = function(v, t, c) {
			self.doRequest(vorlesungTerminURI.replace("{kursID}", v.kursID).replace("{semester}", v.semester).replace("{vorlesungsID}", v.id).replace("{terminID}", t.id), "PUT", t, c);		
		};
		
		this.deleteVorlesungTermin = function(v, t, c) {
			self.doRequest(vorlesungTerminURI.replace("{kursID}", v.kursID).replace("{semester}", v.semester).replace("{vorlesungsID}", v.id).replace("{terminID}", t.id), "DELETE", null, c);		
		};
		
		this.getAllConflictsTermineKurs = function(v, t, c) {
			self.doRequest(termineKursURI.replace("{datum}", t.datum).replace("{kursID}", v.kursID), "GET", null, c);
		};
		
		this.getAllConflictsTermineDozent = function(v, t, c) {
			self.doRequest(termineDozentURI.replace("{datum}", t.datum).replace("{dozentID}", v.dozentID), "GET", null, c);
		};
		
		this.getAllConflictsTermineRaum = function(t, c) {
			self.doRequest(termineRaumURI.replace("{datum}", t.datum).replace("{raum}", t.raum), "GET", null, c);
		};
		
		this.getKursPlanURI = function(k, s) {
			return rootURI + birtURI + kursPlanURI.replace("{kursID}", k.id).replace("{semester}", s);	
		};
		
		this.getKursPlanQuerURI = function(k, s) {
			return rootURI + birtURI + kursPlanQuerURI.replace("{kursID}", k.id).replace("{semester}", s);	
		};
		
		this.getDozentenPlanURI = function(v) {
			return rootURI + birtURI + dozentenPlanURI.replace("{vorlesungID}", v.id);
		};
		
		this.doRequest = function(uri, method, data, callback, callbackData) {
			var api = new Object();
			if(uri === undefined || uri === null || method === undefined || method === null || allowedMethods.indexOf(method) === -1) {
				api.isError = true;
				api.status = "0";
				api.response = "";
				callback(api, callbackData);
			}
			var xhr = new XMLHttpRequest();
			xhr.onreadystatechange = function() {
				if(xhr.readyState === 4) {
					if(xhr.response !== null && (xhr.status === 200 || xhr.status === 201 || xhr.status === 204)) { //OK, ACCEPTED, NO_CONTENT
						api.isError = false;
						api.status = xhr.status;
						try {
							api.response = JSON.parse(xhr.response);	
						} catch(e) {
							api.response = "";
						}
					} else { //ANY HTTP ERROR
						if (xhr.response === null) { //NO WEBSERVICE ERROR
							api.isError = true;
							api.status = xhr.status;
							api.response = xhr.statusText;
						} else { //WEBSERVICE ERROR
							api.isError = true;
							try {
								api.status = JSON.parse(xhr.response).status; 
								api.response = JSON.parse(xhr.response).message;	
							} catch(e) {
								api.status = xhr.status;
								api.response = xhr.statusText;
							}
						}
					}
					callback(api, callbackData); //GO TO CALLBACK
				}
			};
			//NETWORK ERROR
			xhr.onerror = function() {
				api.isError = true;
				api.status = "0";
				api.response = "";
				callback(api, callbackData);
			};
			xhr.open(method, rootURI+apiURI+uri, true);
			if(data !== undefined && data !== null && (method === "POST" || method === "PUT")) {
				var dataString = JSON.stringify(data);
				xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
				xhr.send(dataString);
			} else {
				xhr.send();
			}
		};
	};
};