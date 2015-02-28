(function() {
"use strict";

var DEFAULT_ROUTE = 'stammdaten'; //TODO dashboard

var template = document.querySelector('#home');

template.homePages = [
	{name: 'Stammdaten', hash: 'stammdaten', html: '<vvs-uebersicht></vvs-uebersicht>', loaded: false},
	{name: 'Link1', hash: 'link1', html: null, loaded: true},
	{name: 'Link2', hash: 'link2', html: null, loaded: true},
	{name: 'Link3', hash: 'link3', html: null, loaded: true},
	{name: 'Link4', hash: 'link4', html: null, loaded: true},
	{name: 'Link5', hash: 'link5', html: null, loaded: true},
	{name: 'Link6', hash: 'link6', html: null, loaded: true},
	{name: 'Link7', hash: 'link7', html: null, loaded: true}
];

function handleHashChange(reload) {
	template.route = location.hash.substring(1, location.hash.length);
	if (!template.route) {
		template.route = DEFAULT_ROUTE;
	}
	var nextPage = findPage(template.route);
	if (reload === true) {
		nextPage.loaded = false;
	}
	if(!nextPage.loaded) {
		nextPage.loaded = true;
		template.injectBoundHTML(nextPage.html, document.querySelector('#home-pages-'+nextPage.hash));
	}
}

function findPage(hash) {
	var i;
	for (i=0; i<template.homePages.length; i++) {
		if (template.homePages[i].hash === hash) {
			return template.homePages[i];
		}
	}
	return {name:'', hash: '', html:'', loaded: true};
}

window.addEventListener('hashchange', handleHashChange);

template.addEventListener('template-bound', function(e) {
	if(location.hash) {
		handleHashChange();
	} else {
		this.route = DEFAULT_ROUTE;
		
		location.hash = '#' + DEFAULT_ROUTE;
	}
});

template.menuItemSelected = function(e, detail, sender) {
	if (detail.isSelected) {
		document.querySelector('#home-scaffold').closeDrawer();
	}
};

template.navigateFromDrawer = function(e, detail, sender) {
  	var selectedPage = e.target.templateInstance.model.page;
	location.href = '#' + selectedPage.hash;
};

template.reload = function(e, detail, sender) {
	handleHashChange(true);
};

})();