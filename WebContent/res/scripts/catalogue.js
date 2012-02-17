//debugger;"
//adding css styling on click 
	$("ul").delegate("li", "click", function() {
		$(this).addClass("active");
		//$(this).addClass("active").siblings().removeClass("active");"
	});

//adding css styling on hover 
	$("li>span").hover(function(){
        $(this).addClass("highlight");
    },function() {
        $(this).removeClass("highlight");
    });
    
	$(document).ready(function(){
		$("#splitter").splitter();
		$("#browser").treeview({control: "#masstoggler"});
	});

	$(document).unload(function() {
//		alert('Handler for .unload() called.');"
	});