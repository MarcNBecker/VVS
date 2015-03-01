"use strict";
var model = new function() {
	/*
	 * Helper functions
	 */
	this.helper = new function() {
		var FachInstanzComplete = function() { //Use this only with ModulplanComplete and ModulInstanzComplete
			this.id = 0;
			this.fach = new model.templates.Fach();
			this.semester = 0;
			this.stunden = 0;
		};
		
		var ModulInstanzComplete = function() { //Use this only with ModulplanComplete to store FachInstanzen with a ModulInstanz
			this.id = 0;
			this.modul = new model.templates.Modul();
			this.credits = 0;
			this.fachInstanzList = [];
		};
		
		var ModulplanComplete = function() {
			this.id = 0;
			this.studiengang = "";
			this.vertiefungsrichtung = "";
			this.modulInstanzList = [];
		};
		
		this.getModulplanComplete = function(m, c) {
			//Read basic modulplan information
			model.webService.getModulplan(m, function(api1) {
				//Modulplan exists
				if(!api1.isError) {
					var modulplan = new ModulplanComplete();
					modulplan.id = api1.response.id;
					modulplan.studiengang = api1.response.studiengang;
					modulplan.vertiefungsrichtung = api1.response.vertiefungsrichtung;
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
							//Parse list of ModulInstanz
							for(var i=0; i<api2.response.length; i++) {
								var currentModulInstanz = api2.response[i];
								var completeModulInstanz = new ModulInstanzComplete();
								completeModulInstanz.id = currentModulInstanz.id;
								completeModulInstanz.modul = currentModulInstanz.modul;
								completeModulInstanz.credits = currentModulInstanz.credits;
								//Add ModulInstanz to Modulplan
								modulplan.modulInstanzList.push(completeModulInstanz);
								//Read FachInstanzen
								model.webService.getAllFachInstanzen(currentModulInstanz, function(api3) {
									//FachInstanzList can be read
									if(!api3.isError) {
										for(var ii=0; ii<api3.response.length; ii++) {
											var currentFachInstanz = api3.response[ii];
											var completeFachInstanz = new FachInstanzComplete();
											completeFachInstanz.id = currentFachInstanz.id;
											completeFachInstanz.fach = currentFachInstanz.fach;
											completeFachInstanz.semester = currentFachInstanz.semester;
											completeFachInstanz.stunden = currentFachInstanz.stunden;
											//Search correct completeModulInstanz; can't rely on outer scope
											for(var iii=0; iii<modulplan.modulInstanzList.length; iii++) {
												if(modulplan.modulInstanzList[iii].id === currentFachInstanz.modulInstanzID) {
													modulplan.modulInstanzList[iii].fachInstanzList.push(completeFachInstanz);			
													break;
												}
											}
										}
									}
									//Every ModulInstanz triggers a FachInstanzList call (deepRun)
									deepRuns++;
									//Last call is triggered, then go to callback
									if(api2.response.length === deepRuns) {
										c(modulplan);
									}
								});
							}
						} else { //Reading ModulInstanzList failed, return Modulplan
							c(modulplan);
						}
					});
				} else { //Reading Modulplan failed, return null
					c(null);
				}
			});
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
		};

		this.FachInstanz = function() {
			this.id = 0;
			this.fach = new model.templates.Fach();
			this.modulInstanzID = 0;
			this.semester = 0;
			this.stunden = 0;
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
			this.studentenAnzahl = 0;
			this.kurssprecherVorname = "";
			this.kurssprecherName = "";
			this.kurssprecherMail = ""; //Valid mail
			this.kurssprecherTelefon = ""; //Valid phone
			this.studiengangsleiterID = 0;
			this.sekretariatName = "";
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
			this.credits = 0;
		};

		this.Modulplan = function() {
			this.id = 0;
			this.studiengang = "";
			this.vertiefungsrichtung = "";
		};

		this.Studiengangsleiter = function() {
			this.id = 0;
			this.name = "";
		};
		
		this.User = function() {
			this.name = "";
			this.passwort = "";
			this.repraesentiert = 0; //An ID of a Studiengangsleiter
		};
		
	};
	
	/*
	 * WebService calls
	 */
	this.webService = new function() {

		var self = this;
		var allowedMethods = ["GET", "POST", "PUT", "DELETE"];
		var rootURI = "http://localhost:8080/VVS/api/v1";
		
		var dozentenURI = "/dozenten";
		var dozentURI = "/dozenten/{dozentID}";
		var dozentFaecherURI = "/dozenten/{dozentID}/faecher";
		var dozentFachURI = "/dozenten/{dozentID}/faecher/{fachID}";
		var dozentKommentareURI = "/dozenten/{dozentID}/kommentare";
		var dozentKommentarURI = "/dozenten/{dozentID}/kommentare/{kommentarID}";

		var kurseURI = "/kurse";
		var kursURI = "/kurse/{kursID}";
		var kursBlocklagenURI = "/kurse/{kursID}/blocklagen";
		var kursBlocklageURI = "/kurse/{kursID}/blocklagen/{semester}";
		
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
		
		this.getAllDozentKommentare = function(d, c) {
			self.doRequest(dozentKommentareURI.replace("{dozentID}", d.id), "GET", null, c);
		};
		
		this.createDozentKommentar = function(k, c) {
			self.doRequest(dozentKommentareURI.replace("{dozentID}", k.dozentID), "POST", k, c);
		};
		
		this.deleteDozentKommentar = function(k, c) {
			self.doRequest(dozentKommentarURI.replace("{dozentID}", k.dozentID).replace("{kommentarID}", k.id), "DELETE", null, c);	
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
		
		this.createUser = function(u, c) {
			self.doRequest(userAllURI, "POST", u, c);
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
		
		this.createModulInstanzWithModul = function(mi, c) {
			self.doRequest(modulInstanzenURI.replace("{modulplanID}", mi.modulplanID), "POST", mi, c);	
		};
		
		this.createModulInstanz = function(mi, c) {
			self.doRequest(modulInstanzURI.replace("{modulplanID}", mi.modulplanID).replace("{modulID}", mi.modul.id), "PUT", mi, c);
		};
		
		this.deleteModulInstanz = function(mi, c) {
			self.doRequest(modulInstanzURI.replace("{modulplanID}", mi.modulplanID).replace("{modulID}", mi.modul.id), "DELETE", null, c);	
		};
		
		this.getAllFachInstanzen = function(mi, c) {
			self.doRequest(fachInstanzenURI.replace("{modulplanID}", mi.modulplanID).replace("{modulID}", mi.modul.id), "GET", null, c);
		};
		
		this.createFachInstanzWithFach = function(mi, fi, c) {
			self.doRequest(fachInstanzenURI.replace("{modulplanID}", mi.modulplanID).replace("{modulID}", mi.modul.id), "POST", fi, c);	
		};
		
		this.createFachInstanz = function(mi, fi, c) {
			self.doRequest(fachInstanzURI.replace("{modulplanID}", mi.modulplanID).replace("{modulID}", mi.modul.id).replace("{fachID}", fi.fach.id), "PUT", fi, c);	
		};
		
		this.deleteFachInstanz = function(mi, fi, c) {
			self.doRequest(fachInstanzURI.replace("{modulplanID}", mi.modulplanID).replace("{modulID}", mi.modul.id).replace("{fachID}", fi.fach.id), "DELETE", null, c);	
		};
		
		this.doRequest = function(uri, method, data, callback) {
			if(uri === undefined || uri === null || method === undefined || method === null || allowedMethods.indexOf(method) === -1) {
				api.isError = true;
				api.status = "0";
				api.response = "";
				callback(api);
			}
			var xhr = new XMLHttpRequest();
			xhr.onreadystatechange = function() {
				if(xhr.readyState === 4) {
					var api = new Object();
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
					callback(api); //GO TO CALLBACK
				}
			};
			//NETWORK ERROR
			xhr.onerror = function() {
				api.isError = true;
				api.status = "0";
				api.response = "";
				callback(api);
			};
			xhr.open(method, rootURI+uri, true);
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