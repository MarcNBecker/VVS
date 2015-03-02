(function() {
"use strict";

var DEFAULT_ROUTE = 'stammdaten'; //TODO dashboard

var template = document.querySelector('#home');

//page parameter are used with data binding to exchange data between home and the vvs polymer elements
//TODO can we add them to the URL hash?
template.pageParameter = {
	dozent: {
		id: 0
	}
};

//contains the loading state for each page; can be exchanged via data binding
template.pageLoaded = {
	stammdaten: false,
	dozent: false
};

//Page descriptors including the HTML code to load the page
template.pageDescriptor = {
	stammdaten: {name: 'Stammdaten', hash: 'stammdaten', html: '<vvs-uebersicht pageParameter="{{pageParameter}}" pageLoaded="{{pageLoaded}}" pageDescriptor="{{pageDescriptor}}" refresh="{{refresh}}"></vvs-uebersicht>'},
	dozent: {name: 'Dozent pflegen', hash: 'dozent', html: '<vvs-dozent dozentID="{{pageParameter.dozent.id}}" refresh="{{refresh}}" loaded="{{pageLoaded.dozent}}"></vvs-dozent>'},
};

//Pages to display in the navigation drawer
template.drawerPages = [template.pageDescriptor.stammdaten];

//Pages that can be displayed in the home main div
template.homePages = [template.pageDescriptor.stammdaten, template.pageDescriptor.dozent]

//Is called whenever the url hash is changed and navigates the app
function handleHashChange(refresh) {
	template.route = location.hash.substring(1, location.hash.length);
	if (!template.route) { //No hash exists
		template.route = DEFAULT_ROUTE;
	}
	var nextPage = findPage(template.route);
	if (refresh === true) { //Forced refresh?
		template.pageLoaded[nextPage.hash] = false;
	}
	if(!template.pageLoaded[nextPage.hash]) { //Inject vvs polymer element to correct page
		template.pageLoaded[nextPage.hash] = true;
		template.injectBoundHTML(nextPage.html, document.querySelector('#home-pages-'+nextPage.hash));
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
	if(location.hash) { //Hash already set
		handleHashChange(); 
	} else {
		this.route = DEFAULT_ROUTE;
		location.hash = '#' + DEFAULT_ROUTE; //Update hash manually
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
	location.href = '#' + selectedPage.hash;
};

//Force a refresh for the current page
template.refresh = function(e, detail, sender) {
	handleHashChange(true);
};

})();