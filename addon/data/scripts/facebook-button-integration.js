console.log("Manipulating DOM");


$("div[id^='hyperfeed_story_id']").each(function() {
	var story_id = $(this).attr("id");
	var story_link = story_id + "_link";
	$("<div style=\"padding: 12px 12px 10px; border-top: 1px solid #e5e5e5; background-color: #ffffb4;\">" +
			"<span>" +
			"<a id=\"" + story_link + "\" href=\"#\" style=\"color: #7f7f7f; display: inline-block; font-size: 12px; font-weight: bold;\">Sag die Wahrheit!" +
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
});

self.port.on("responseNews",function(payload) {
	if (payload.success) {
		var toAppend = "<ul>";
		$.each(payload.content.news, function( index, value ){
			toAppend += "<li><a target=\"_blank\" href=\"" + value.url + "\">" + value.source + ": " + value.headline + "</a></li>"; 
		});
		toAppend += "</ul>";
		$(toAppend).appendTo($("#" + payload.id));
	} else {
		console.log("Ne: " + payload.content);
	}
	
});

console.log("Done");
