

var data = {}


$("a").click(function(){

	$(this).removeClass("btn-info");
	$(this).addClass("btn-success");

	var td = $(this).parent();
	var tr = td.parent();

	var selectTag = tr.children()[3].children[0];
	$(selectTag).removeAttr("disabled");

	data["clickDate"] = new Date();

});



$(".note").change(function(){

	var td = $(this).parent();
	var tr = td.parent();
	var a = tr.children()[4].children[0];


	if($(this).val() == "none")
	{
		$(a).addClass("disabled");
		$(a).attr("disabled", "disabled");
	}
	else
	{
		$(a).removeAttr("disabled");
		$(a).removeClass("disabled");
	}
});



$(".send_news").click(function(){
	var me = $(this);

	var td = $(this).parent();
	var tr = td.parent();
	var select = tr.children()[3].children[0];


	if($(select).val() == "none"){
		alert("Nu ati dat nici o nota!");
	}
	else{
		var  ul = td.children()[1];

		var title = $(ul.children[0]).text();
		var description = $(ul.children[1]).text();
		var link = $(ul.children[2]).text();

		data["note"] = $(select).val();
		data["title"] = title;
		data["description"] = description;
		data["link"] = link;
		data["sendDate"] = new Date();	//get send date

		$.ajax({
			type: "POST",
			contentType: "application/json",
			url: "/insertNews",
			data: JSON.stringify(data),
			dataType: 'json',
			timeout: 600000,
			success: function(data){
				console.log("success: ", data);
				if(data == "1")
				{
					$(me).addClass("disabled");
					$(me).attr("disabled", "disabled");

					var selectTag = tr.children()[3].children[0];
					$(selectTag).addClass("disabled");
					$(selectTag).attr("disabled", "disabled");

					$(tr).css("background-color", "red");
				}
			},
			error: function(e){
			    alert("Eroare: " + e);
				console.log("Error: " + e);
			},
			done: function(e){
				console.log("done: " + e);
			}
		});

	}
});



