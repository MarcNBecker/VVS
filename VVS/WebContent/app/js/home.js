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

//Page descriptors inclduing the HTML code to load the page
template.pageDescriptor = {
	stammdaten: {name: 'Stammdaten', hash: 'stammdaten', html: '<vvs-uebersicht></vvs-uebersicht>', loaded: false},
	dozent: {name: 'Dozent pflegen', hash: 'dozent', html: '<vvs-dozent dozentID="{{pageParameter.dozent.id}}" refresh="{{refresh}}" loaded="{{pageDescriptor.dozent.loaded}}"></vvs-dozent>', loaded: false},
	link2: {name: 'Link2', hash: 'link2', html: null, loaded: true}
};

//Pages to display in the navigation drawer
template.homePages = [template.pageDescriptor.stammdaten, template.pageDescriptor.dozent, template.pageDescriptor.link2];

//Is called whenever the url hash is changed and navigates the app
function handleHashChange(refresh) {
	template.route = location.hash.substring(1, location.hash.length);
	if (!template.route) { //No hash exists
		template.route = DEFAULT_ROUTE;
	}
	var nextPage = findPage(template.route);
	if (refresh === true) { //Forced refresh?
		nextPage.loaded = false;
	}
	if(!nextPage.loaded) { //Inject vvs polymer element to correct page
		nextPage.loaded = true;
		template.injectBoundHTML(nextPage.html, document.querySelector('#home-pages-'+nextPage.hash));
	}
}

//Finds a page or returns a dummy page object
function findPage(hash) {
	var page = template.pageDescriptor[hash];
	if(page) {
		return page;
	} else {
		return {name:'', hash: '', html:'', loaded: true};	
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