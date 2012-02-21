//debugger;"
//This hashmap contains the details of the measurements that are shown when we click on a tree leaf. 
HashMap = function(){
			  this._dict = [];
			}
			HashMap.prototype._get = function(key){
			  for(var i=0, couplet; couplet = this._dict[i]; i++){
			    if(couplet[0] === key){
			      return couplet;
			    }
			  }
			}
			HashMap.prototype.put = function(key, value){
			  var couplet = this._get(key);
			  if(couplet){
			    couplet[1] = value;
			  }else{
			    this._dict.push([key, value]);
			  }
			  return this; // for chaining
			}
			HashMap.prototype.get = function(key){
			  var couplet = this._get(key);
			  if(couplet){
			    return couplet[1];
			  }
			}
			
function createHashMap(key, content)	{
				map.put(key, content);
				
			}
			
			function getHashMapContent(key){
				
				var value = map.get(key);
				$('#details').empty();
				$('#details').append(value);
				
			}
			
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
		
		var $scrollingDiv = $("#scrollingDiv");
		 
		$(window).scroll(function(){			
			$scrollingDiv
				.stop()
				.animate({"marginTop": ($(window).scrollTop() + 30) + "px"}, "slow" );			
		});


	});

	$(document).unload(function() {
//		alert('Handler for .unload() called.');"
	});
	
	
	
	
	
	