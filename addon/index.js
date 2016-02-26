var self = require("sdk/self");
var tabs = require("sdk/tabs");
var panels = require("sdk/panel");
var Request = require("sdk/request").Request;
var { ToggleButton } = require('sdk/ui/button/toggle');

var isFacebookTabOpen = -1;
var integratedForActiveTab = false;

// Listen for tab content loads.
tabs.on('ready', function(tab) {
	checkTab(tab);
});
// Listen for tab switches.
tabs.on('activate', function(tab) {
	checkTab(tab);
});

function checkTab(tab) {
	isFacebookTabOpen = tab.url.indexOf("facebook.com");
	integratedForActiveTab = false;
}

var panel = panels.Panel({
	contentURL : self.data.url("wrong-active-tab-panel.html"),
	onHide : uncheckButton
});


var button = ToggleButton({
	id : "lyingPressButton",
	label : "Was sagt die Lügenpresse dazu?",
	icon : {
		"16" : "./images/icon_16.png",
		"32" : "./images/icon_32.png",
		"64" : "./images/icon_64.png"
	},
	onChange : handleChange
});

function handleChange(state) {
	if (state.checked) {
		if (isFacebookTabOpen < 0) {
			panel.show({
				position : button
			});
		} else if (!integratedForActiveTab) {
			console.log('Integrating buttons...');
			worker = require("sdk/tabs").activeTab.attach({
				contentScriptFile : [ self.data.url("scripts/jquery-2.2.1.min.js"),
				                      self.data.url("scripts/facebook-button-integration.js") ]
			});
			worker.port.on("requestNews", function(payload) {
				var dataObj = {
					text : payload.text
				};
				Request( {
					url : "http://localhost:8081/news/find",
					content : dataObj,
					contentType : "application/json",
					onComplete : function(response) {
						if (response.status === 200) {
							var responseObj = {
								success : true,
								content : response.json,
								id : payload.id
							}
							worker.port.emit("responseNews", responseObj);	
						} else {
							var responseObj = {
								success : false,
								content : response.status
							}
							worker.port.emit("responseNews", responseObj);
						}
					}
				}).post();
			});
			integratedForActiveTab = true;
			uncheckButton();
		} else {
			uncheckButton();
		} 
	}
}

function uncheckButton() {
	button.state('window', {
		checked : false
	});
}
