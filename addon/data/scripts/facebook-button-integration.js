console.log("Manipulating DOM");

var addButton = function() {
	var story_id = $(this).attr("id");
	var story_link = story_id + "_link";
	$("<div class=\"factbuddy\" style=\"margin: 10px 0 -12px 0; padding: 9px 0; border-top: 1px solid #e5e5e5;\">" +
			"<span>" +
			'<a class="factbuddy-open" id="' + story_link + '" href="#" style="color: #7f7f7f; font-size: 12px; font-weight: bold;"><img src="' + self.options.buttonLogoUrl + '" style="float: left; margin-top: -2px; margin-right: 7px;">Frag Fact Buddy!' +
			"</a>" +
			"</span>" +
			"</div>").appendTo($(this).find(".userContentWrapper div:first-child").first());
	$("#" + story_link).click(function() {
		var postText = $("#" + story_id + " .userContent").text();
		postText += " " + $("#" + story_id + " .userContent + div" ).text();
		var dataObj = {
			text : postText,
			id : story_id
		};
		self.port.emit("requestNews", dataObj);
	});
};

$("div[id^='hyperfeed_story_id']").each(addButton);

$("div[id^='topnews_main_stream']").observe("childlist subtree", "div[id^='hyperfeed_story_id']", function(record) {
	$("div[id^='hyperfeed_story_id']").each(function() {
		if($(this).find('.factbuddy').length === 0) {
			$(this).each(addButton);
		}	
	});
});

self.port.on("responseNews",function(payload) {

	if (payload.success) {
		var blockToAppend = '<div class="factbuddy-container" id="factbuddy-container-' + payload.id + '">' +
		'<div class="factbuddy-borderimage"><img src="' + self.options.borderUrl + '" height="7px" width="15px"></div>' +
		'<div class="factbuddy-header"><img height="44px" width="44px" src="' + self.options.logoUrl + '">' +
		'<div class="factbuddy-headline">Fact Buddy</div><a href="#" class="factbuddy-close" id="factbuddy-close-' + payload.id + '">schlie&szlig;en</a></div>' +
		'<div class="factbuddy-content">';
		
		$.each(payload.content.news, function( index, value ) {
			var imageUrl = self.options.noImageUrl;
			if (value.imageUrl) {
				imageUrl = value.imageUrl
			}
			blockToAppend += '<div class="factbuddy-entry">' +
			'<div class="factbuddy-entry-image"><img width="80px" src="' + imageUrl + '" alt="' + value.headline + '"></div>' +
			'<h1>' + value.headline + '</h1>' +
			'<div class="factbuddy-entry-text">' + value.shortText + '</div>' +
			'<div class="factbuddy-entry-source">Quelle: ' + value.source + '</div>' +
			'<div class="factbuddy-entry-buttons"><a href="'+ value.url +'" target="_blank">Artikel lesen</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#">In Kommentar einfügen</a></div>' +
			'</div>'; 
		});
		blockToAppend += "</div></div>";
		$(blockToAppend).insertAfter($("#" + payload.id + " .factbuddy"));
		
		$('#factbuddy-container-' + payload.id).show();
		$('#factbuddy-close-' + payload.id).click(function() {
			$('#factbuddy-container-' + payload.id).remove();
		}); 
	} else {
		console.log("Ne: " + payload.content);
		var blockToAppend = '<div class="factbuddy-container" id="factbuddy-container-' + payload.id + '">' +
		'<div class="factbuddy-borderimage"><img src="' + self.options.borderUrl + '" height="7px" width="15px"></div>' +
		'<div class="factbuddy-header"><img height="44px" width="44px" src="' + self.options.logoUrl + '">' +
		'<div class="factbuddy-headline">Fact Buddy</div><a href="#" class="factbuddy-close" id="factbuddy-close-' + payload.id + '">schlie&szlig;en</a></div>' +
		'<div class="factbuddy-content">Keine passenden Beiträge gefunden!</div></div>';
		$(blockToAppend).insertAfter($("#" + payload.id + " .factbuddy"));
		
		$('#factbuddy-container-' + payload.id).show();
		$('#factbuddy-close-' + payload.id).click(function() {
			$('#factbuddy-container-' + payload.id).remove();
		});
	}
	
});

console.log("Done");

$('head').append('<style>' +
		'.factbuddy-container {' +
		'	position: absolute;' +
		'	margin-left: -5px;' +
		'	margin-top: 10px;' +
		'	height:300px;' +
		'	width:400px;' +
		'	background-color: #f5f5f5;' +
		'	display:block;' +
		'	z-index:1002;' +
		'	border-radius: 5px;' +
		'	-webkit-box-shadow: 0px 0px 5px 0px rgba(0,0,0,0.5);' +
		'	-moz-box-shadow: 0px 0px 5px 0px rgba(0,0,0,0.5);' +
		'	box-shadow: 0px 0px 5px 0px rgba(0,0,0,0.5);' +
		'}' +
		'.factbuddy-header {' +
		'	border-bottom: 1px solid #ccc;' +
		'	display: block;' +
		'	height: 48px;' +
		'	padding: 2px;' +
		'}' +
		'.factbuddy-header img {' +
		'	float: left;' +
		'	margin: 0 10px 0 2px;' +
		'}' +
		'.factbuddy-borderimage {' +
		'	position:absolute;' +
		'	left: 7px;' +
		'	top:-11px;' +
		'}' +
		'.factbuddy-headline {' +
		'	font-size: 18px;' +
		'	font-family: sans-serif;' +
		'	line-height: 52px;' +
		'	color: #222;' +
		'	font-weight: bold;' +
		'}' +
		'.factbuddy-close {' +
		'	position: absolute;' +
		'	top: 21px;' +
		'	right: 10px;' +
		'	font-size: 8pt;' +
		'	text-decoration: none;' +
		'	font-family: sans-serif;' +
		'}' +
		'.factbuddy-content {' +
		'	margin: 0 6px 0 6px;' +
		'	display: block;' +
		'	overflow-y: scroll;' +
		'	height: 245px;' +
		'	padding-top: 2px' +
		'}' +
		'.factbuddy-entry {' +
		'	clear: both;' +
		'	display: inline-block;' +
		'	margin: 4px 0 4px 0;' +
		'	width: 100%;' +
		'}' +
		'.factbuddy-entry-image {' +
		'	float: left;' +
		'	margin: 0 10px 10px 0;' +
		'	height: 75px;' +
		'	width: 80px;' +
		'}' +
		'.factbuddy-entry h1 {' +
		'	font-size: 16px;' +
		'	font-weight: normal;' +
		'	margin: 0;' +
		'	padding: 2px 0 4px 0;' +
		'}' +
		'.factbuddy-entry-text {' +
		'	font-size: 12px;' +
		'	float: right;' +
		'	width: 298px;' +
		'}' +
		'.factbuddy-entry-buttons {' +
		'	clear: both;' +
		'	padding: 7px 0 5px 0;' +
		'	border-top: 1px solid #ccc;' +
		'	border-bottom: 1px solid #ccc;' +
		'	text-align: right;' +
		'	font-size: 12px;' +
		'	line-height: 12px;' +
		'}' +
		'.factbuddy-entry-buttons a {' +
		'	color: #999;' +
		'	text-decoration: none;' +
		'	margin: 0 0 40px 0;' +
		'}' +
		'.factbuddy-entry-buttons a:hover {' +
		'	color: #999;' +
		'	text-decoration: underline;' +
		'}' +
		'.factbuddy-entry-source {' +
		'	font-size: 10px;' +
		'	color: #999;' +
		'	text-transform: uppercase;' +
		'	float: right;' +
		'	width: 298px;' +
		'	margin: 8px 0 8px 0;' +
		'}' +
		'.factbuddy-open {' +
		'	display: block;' +
		'}' +
		'</style>');
