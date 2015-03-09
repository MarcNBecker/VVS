(function() {
"use strict";

//Logout
sessionStorage.removeItem("user");

var template = document.querySelector('#login');

//Login
template.login = function() {
	if(template.name && template.passwort) {
		var user = new model.templates.User();
		user.name = template.name;
		user.passwort = template.passwort;
		model.webService.authenticateUser(user, function(api) {
			if(!api.isError) {
				//Set LocalStorage cookie and navigate to home
				sessionStorage.setItem("user", JSON.stringify(api.response));
				location.href = 'app/html/home.html' + (sessionStorage.getItem("targetHash") ? sessionStorage.getItem("targetHash"): "");
			} else {
				if(api.status === 31) {
					template.$.toast_auth_error.show();
				} else {
					template.$.toast_error.show();
				}
			}
		});
	} else {
		return;
	}
};

})();
