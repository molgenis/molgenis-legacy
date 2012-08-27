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
	$("#browser").treeview({
		control:"#masstoggler",
	});
	
	var $scrollingDiv = $("#scrollingDiv");

	$(window).scroll(function(){			
		$scrollingDiv
		.stop()
		.animate({"marginTop": ($(window).scrollTop() + 30) + "px"}, "slow" );			
	});
	
	$(document).ready(function(){$('ul#browser li').show();});

	$('#expand').click(function(){$('#browser').find('ul').show();});
	
	$('#collapse').click(function(){$('#browser').find('ul').hide();});
	
	$('#expand').trigger('click');
	
});