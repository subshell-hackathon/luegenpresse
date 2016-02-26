

function getTextFromNode(node) {
	return $(node).text();
} 

var text = getTextFromNode(".userContentWrapper");

console.log("Text: ", text);

console.log("Manipulating DOM");


$("div[id^='hyperfeed_story_id']").each(function() {
	var story_id = $(this).attr("id");
	$("<div style=\"padding: 12px 12px 10px; border-top: 1px solid #e5e5e5; background-color: #ffffb4;\">" +
			"<span>" +
			"<a id=\"" + story_id + "_link\" href=\"#\" style=\"color: #7f7f7f; display: inline-block; font-size: 12px; font-weight: bold;\">Sag die Wahrheit!" +
			"</a>" +
			"</span>" +
			"</div>").appendTo($(this));
	$("#" + story_id + "_link").click(function() {
		var postText = $("#" + story_id).text();
		$.post("http://localhost:8081/");
	}
});

console.log("Done");