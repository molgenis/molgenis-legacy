/* Adapted to accomodate MOLGENIS.
 * In particular, ability to work with NULL options (for nillable lookups)*/
(function() {
  (function($) {
    return $.fn.ajaxChosen = function(options, callback) {
      //alert("one");
      var select;
      select = this;
      this.chosen(options);
      
      var refresh = function(evt) {
      	//alert("two");
          var field, val;
          val = $.trim($(this).attr('value'));
          if (val === $(this).data('prevVal')) {
            return false;
          }
          $(this).data('prevVal', val);
          field = $(this);
          options.data = {
          	//ADAPTED FOR MOLGENIS
          	xref_label_search: val,
          	xref_entity: options.xref_entity,
          	xref_field: options.xref_field,
          	xref_label: options.xref_label
          };        
          if (typeof success !== "undefined" && success !== null) {
            success;
          } else {
            success = options.success;
          };
          options.success = function(data) {
            //document.write("succes: "+data);
            var items;
            if (!(data != null)) {
              return;
            }
            selected = [];
            select.find('option').each(function() {
              //keep the selected option as first option
              if (!$(this).is(":selected")) {
            	  return $(this).remove();
              } else {
            	  selected[$(this).attr("value")] = $(this);
            	  //console.log(selected.attr("value"));
              }
            });

            items = callback(data);
            if( !select.attr('multiple') && !select.hasClass("required") ) {
            	$("<option value=\"\">&nbsp;</option>").prependTo(select);
            } 
        	$.each(items, function(value, text) {
        		if(selected != null && selected[value] == null) {
        			return $("<option />").attr('value', value).html(text).appendTo(select);
        		}
        	});
            
            select.trigger("liszt:updated");

            field.attr('value', val);
            if (typeof success !== "undefined" && success !== null) {
              return success();
            }
          };
          options.error = function(XMLHttpRequest, textStatus, errorThrown)
          {
               alert(textStatus + " | " + errorThrown);     // throw ParserError
          }
          return $.ajax(options);
        }
      
      if(select.attr('multiple'))
      {
    	  //this.next('.chzn-container').find(".search-field > input").bind('focus', refresh);
    	  //return this.next('.chzn-container').change(refresh);
    	  this.next('.chzn-container').click(refresh);
    	  return this.next('.chzn-container').find(".search-field > input").bind('keyup', refresh);
      }
      else
      {
    	  this.next('.chzn-container').click(refresh);
    	  return this.next('.chzn-container').find(".chzn-search > input").bind('keyup', refresh);
      }
    };
  })(jQuery);
}).call(this);
