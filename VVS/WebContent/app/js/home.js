(function() {
"use strict";

var DEFAULT_ROUTE = 'stammdaten';

var template = document.querySelector('#home');

template.user = JSON.parse(sessionStorage.getItem("user"));

//Home toasts: intialized on template bound
template.toasts = {
	success: null,
	error: null,
	constraint: null
};

//page parameter are used with data binding to exchange data between home and the vvs polymer elements (structure should follow template objects)
//if possible you should set the pageParameter via a hash change to enable consistent navigation
template.pageParameter = {
	dozent: {
		id: 0
	},
	modulplanPflegen: {
		id: 0
	},
	kurs: {
		id: 0
	},
	user: {
		id: 0
	},
	feiertage: {
		id: 0 //is a year, name id is necessary for correct handling by other functions
	},
	planung: {
		id: 0
	},
	vdashboard: {
		id: 0
	}
};

//contains the loading state for each page; can be exchanged via data binding
template.pageLoaded = {
	stammdaten: false,
	dozent: false,
	modulplanAnlegen: false,
	modulplanPflegen: false,
	kurs: false,
	user: false,
	feiertage: false,
	planung: false,
	vdashboard: false,
	sdashboard: false
};

//Page descriptors including the HTML code to load the page
template.pageDescriptor = {
	stammdaten: {name: 'Stammdaten', hash: 'stammdaten', html: '<vvs-uebersicht pageLoaded="{{pageLoaded}}" pageDescriptor="{{pageDescriptor}}" toasts="{{toasts}}"></vvs-uebersicht>'},
	dozent: {name: 'Dozent pflegen', hash: 'dozent', html: '<vvs-dozent pageParameter="{{pageParameter}}" pageLoaded="{{pageLoaded}}" toasts="{{toasts}}"></vvs-dozent>'},
	modulplanAnlegen: {name: 'Modulplan anlegen', hash: 'modulplanAnlegen', html: '<vvs-modulplan-anlegen pageLoaded="{{pageLoaded}}" toasts="{{toasts}}"></vvs-modulplan-anlegen>'},
	modulplanPflegen: {name: 'Modulplan pflegen', hash: 'modulplanPflegen', html: '<vvs-modulplan-pflegen pageParameter="{{pageParameter}}" pageLoaded="{{pageLoaded}}" toasts="{{toasts}}"></vvs-modulplan-pflegen>'},
	kurs: {name: 'Kurs pflegen', hash: 'kurs', html: '<vvs-kurs pageParameter="{{pageParameter}}" pageLoaded="{{pageLoaded}}" toasts="{{toasts}}"></vvs-kurs>'},
	user: {name: 'User pflegen', hash: 'user', html: '<vvs-user currentUser="{{user}}" pageParameter="{{pageParameter}}" pageLoaded="{{pageLoaded}}" toasts="{{toasts}}"></vvs-user>'},
	feiertage: {name: 'Feiertage pflegen', hash: 'feiertage', html: '<vvs-feiertage pageParameter="{{pageParameter}}" pageLoaded="{{pageLoaded}}" toasts="{{toasts}}"></vvs-feiertage>'},
	planung: {name: 'Planung', hash: 'planung', html: '<vvs-planung pageParameter="{{pageParameter}}" pageLoaded="{{pageLoaded}}" toasts="{{toasts}}"></vvs-planung>'},
	vdashboard: {name: 'Vorlesungsdashboard', hash: 'vdashboard', html: '<vvs-dashboard-vorlesung pageParameter="{{pageParameter}}" pageLoaded="{{pageLoaded}}" toasts="{{toasts}}"></vvs-dashboard-vorlesung>'},
	sdashboard: {name: 'Dashboard', hash: 'sdashboard', html: '<vvs-dashboard-studiengangsleiter user="{{user}}" pageLoaded="{{pageLoaded}}" toasts="{{toasts}}"></vvs-dashboard-studiengangsleiter>'}
};

//Pages to display in the navigation drawer
template.drawerPages = [template.pageDescriptor.sdashboard, template.pageDescriptor.stammdaten];

//Pages that can be displayed in the home main div
template.homePages = [template.pageDescriptor.stammdaten, 
                      template.pageDescriptor.dozent, 
                      template.pageDescriptor.modulplanAnlegen, 
                      template.pageDescriptor.modulplanPflegen, 
                      template.pageDescriptor.kurs, 
                      template.pageDescriptor.user, 
                      template.pageDescriptor.feiertage, 
                      template.pageDescriptor.planung,
                      template.pageDescriptor.vdashboard,
                      template.pageDescriptor.sdashboard];

//Is called whenever the url hash is changed and navigates the app
function handleHashChange(refresh) {
	//move to top
	document.querySelector('body /deep/ #mainContainer').scrollTop = 0;
	template.route = location.hash.substring(2, location.hash.length);
	if (!template.route) { //No hash exists
		template.route = DEFAULT_ROUTE;
	}
	var hasChangedParams = extractParams(template.route); //Also removes the parameter from template.route
	var nextPage = findPage(template.route);
	if (refresh === true || hasChangedParams === true) { //Forced refresh
		template.pageLoaded[nextPage.hash] = false;
	}
	if(!template.pageLoaded[nextPage.hash]) { //Inject vvs polymer element to correct page
		template.pageLoaded[nextPage.hash] = true;
		template.injectBoundHTML(nextPage.html, document.querySelector('#home-pages-'+nextPage.hash));
	}
}

//Extract params from URL - first (and currently only parameter) is mapped to ID of corresponding pageParameter object
function extractParams() {
	var paramStart = template.route.indexOf('-');
	if(paramStart !== -1) {
		var paramString = template.route.substring(paramStart + 1, template.route.length);
		template.route = template.route.substring(0, paramStart);
		var pageParameterObj = template.pageParameter[template.route];
		if(pageParameterObj) {
			if(paramString) {
				if(pageParameterObj.id != paramString) {
					pageParameterObj.id = paramString;
					return true;
				} else {
					return false;
				}
			} else {
				if(pageParameterObj.id !== 0) {
					pageParameterObj.id = 0;
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	} else {
		var pageParameterObj = template.pageParameter[template.route];
		if(pageParameterObj) {
			if(pageParameterObj.id !== 0) {
				pageParameterObj.id = 0;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}

//Finds a page or returns a dummy page object
function findPage(hash) {
	var page = template.pageDescriptor[hash];
	if(page) {
		return page;
	} else {
		return {name:'', hash: '', html:''};	
	}
}

window.addEventListener('hashchange', handleHashChange);

template.addEventListener('template-bound', function(e) {
	//Reject access, if no user is set
	//This is obviously not secure, but more security than this was not desired
	sessionStorage.removeItem("targetHash");
	if(!template.user) {
		location.href = "../../login.html";
		sessionStorage.setItem("targetHash", location.hash);
		return;
	}
	//Init toasts
	template.toasts.success = template.$.toast_success;
	template.toasts.error = template.$.toast_error;
	template.toasts.constraint = template.$.toast_constraint;
	if(location.hash) { //Hash already set
		handleHashChange(); 
	} else {
		this.route = DEFAULT_ROUTE;
		location.hash = '#!' + DEFAULT_ROUTE; //Update hash manually
	}
});

template.menuItemSelected = function(e, detail, sender) {
	if (detail.isSelected) {
		document.querySelector('#home-scaffold').closeDrawer();
	}
};

//Update hash
template.navigateFromDrawer = function(e, detail, sender) {
  	var selectedPage = e.target.templateInstance.model.page;
	location.href = '#!' + selectedPage.hash;
};

//Force a refresh for the current page
template.refresh = function(e, detail, sender) {
	handleHashChange(true);
};

//Navigate to User overview
template.navigateToUser = function() {
	location.href = "#!" + template.pageDescriptor.user.hash + "-" + template.user.name;
};

template.logout = function() {
	location.href = "../../login.html";
};

//handle redirects
window.addEventListener('popstate', function(event) {
	if(event.state !== null && event.state.redirect === true) {
		window.history.back();
	}
});

})();