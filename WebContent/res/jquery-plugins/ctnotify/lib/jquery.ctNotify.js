/*
 * jQuery ctRotator Plugin
 * Examples and documentation at: http://thecodecentral.com/2011/08/15/ctnotify-a-flexible-multi-instance-jquery-notification-script
 * Under MIT license http://www.opensource.org/licenses/mit-license.php
 *
 * @author: Cuong Tham
 * @version: 1.0
 * @requires jQuery v1.2.6 or later
 *
 * @headsTip: A queued notification script based on jQuery. Supports multiple instances. Highly configurable.
 */


(function($) {



  var defaultInstanceId = 'default';
  var defaultType = 'message';
  var instData = {};
  var loopPreventer = 0;


  
  $.extend({
    ctNotifyOption:function(options, instId){
      createInstnace(options, instId);
     
    },
    ctNotify:function(html, type, instId){
      var inst = getInstance(instId);
      addItem(html, type, inst.id);

      if(!inst.inTimeout){
	    removeItem(inst.id);
      }
    }
  });

  function getInstanceIdFallback(instId){
    if(instId == null){
      return defaultInstanceId;
    }else{
      return instId;
    }
  }

  function getInstance(instId){
    instId = getInstanceIdFallback(instId);

    var inst;
    if(instData[instId] != null){
      inst = instData[instId];
    }else{
      inst = createInstnace(null, instId);
   
    }
  
 
    return inst;
  }

  function createInstnace(options, instId){
    instId = getInstanceIdFallback(instId);
    var inst = initialize(options, instId);
    return inst;
  }


  function getOptions(instId){
    return getInstance(instId).options;
  }
  

  function initialize(options, instId){
    var inst;

    if(instData[instId] && instData[instId].isInitialized){
      inst = instData[instId];
    }else{
      inst = {
        id: instId,
        con:null,
        parentCon: null,
        inTimeout: false,
        isInitialized: false,
        timerId: null,
        stickyItemCount: 0        
      };
    
    }
    


    var opts = {
      delay: 3000,
      id: 'ctNotify_' + instId,
      className: 'ctNotify',
      animated: true,
      animateSpeed: 500,
      animateType: "slideUp",
      appendTo: null,
      sticky: false,
      autoWidth: 'fitWindow', //fit,fitWindow, disabled
      width: null, //if autoWidth is set to other than disabled, this option is not used
      opacity: .7,
      position: "fixed",
      align: null, //horizontal align based on calculation
      bodyIdSuffix: '_body_', //the element ID which contains the notificationc children
      bodyClassName: 'ctNotify_body',
      anchors: {
        top:0,
        left: 0,
        bottom: null,
        right: null
      }, //top, left, bottom, right
      containerRender: null,
      itemRender: null,
      onHide: $.noop
    };
    
    options = $.extend({}, opts, options);


    options.bodyId = opts.id + opts.bodyIdSuffix;

    
    if(options.appendTo == null){
      options.appendTo = $(document.body);
    }
    
    if(options.autoWidth != 'fit' && options.autoWidth != 'fitWindow'){
      options.autoWidth = 'disabled';
    }
    
    
    if(options.width != null){
      options.autoWidth = 'disabled';
    }
    



    inst.options = options;
    
    
 
    initContainer(inst);
    initItemRender(inst);
 
    inst.con = inst.options.containerRender(inst);
    inst.con.hide();
    inst.body = inst.con.find('#' + inst.options.bodyId);
    inst.parentCon = inst.options.appendTo;


   
    

    inst.con.bind('click', inst, function(e){
      //console.log('click', e.data.id, e.data.timerId, e.data.inTimeout);
     
   
      if(e.data.id == undefined){
        return; 
      }
      
     
      
      if(e.data.inTimeout){
        if(e.data.timerId != null){
        clearTimeout(e.data.timerId);
        }
        e.data.inTimeout = false;
      }

      e.data.body.empty();
      hide(e.data);
      e.data.stickyItemCount = 0;
       
    });


    if(inst.parentCon.size() == 0){
      throw ('Parent container ' + opt.appendTo + ' no found.');
    }

    inst.con.appendTo(inst.parentCon);



    if(inst.options.autoWidth != 'disabled'){
      $(window).resize(function(){
        fixWidth(inst); 
      });
    }

    

    inst.isInitialized = true;
    instData[instId] = inst;

//console.log(inst.id, inst.options.sticky);
 
    return inst;
    
  }
  
  
  function testIfAbNormalPosition(position){
    
    return position == 'absolute' || position == 'fixed' || position == 'relative';
  }
 
  function doAlign(inst){
    
    if(inst.options.align == null || !inst.con.is(":visible")){    
      return;  
    }
    
    if(inst.options.align == 'center'){
      if(testIfAbNormalPosition(inst.options.position)){
        inst.con.css({
          left: '50%',
          marginLeft: '-' + (inst.con.width() / 2) + 'px'
        });
      }else{
        inst.con.css({
          marginLeft: 'auto',
          marginRight: 'auto'
        });
      }
    } else if(inst.options.align == 'left'){
      if(testIfAbNormalPosition(inst.options.position)){
        inst.con.css({
          left: '0',
          right: 'auto'
        });
      }else{
        inst.con.css({
          marginRight: 'auto',
          marginLeft: '0'
        });
      }
    }else if(inst.options.align == 'right'){
      if(testIfAbNormalPosition(inst.options.position)){
        inst.con.css({
          right: '0',
          left: 'auto'
        });
      }else{
        inst.con.css({
          marginLeft: 'auto',
          marginRight: '0'
        });
      }
    }
  }
  
  function initContainer(inst){
    if(inst.options.containerRender != null){
      return;
    }
      
    inst.options.containerRender = function(inst){
      var options = inst.options;
      var conWrapper = $('<div></div>').attr({
        id:  options.id,
        title: 'click to close'
      })
      .css({
        opacity: options.opacity,
        position: options.position,
        top: options.anchors.top,
        bottom: options.anchors.bottom,
        left: options.anchors.left,
        right: options.anchors.right
      })
      .addClass(options.className)
      .hide();


      var con = $('<ul></ul>').attr({
        id: options.id + options.bodyIdSuffix
      })
      .addClass(options.bodyClassName);
      

      

      conWrapper.append(con);

      return conWrapper;
    };
    

  }
  
  
  function hide(inst){
    var con = inst.con;
    con.hide();
    inst.options.onHide(inst);
  }
  
  
  function initItemRender(inst){
    if(inst.options.itemRender != null){
      return;
    }
      
    inst.options.itemRender = function(html, itemOptions, inst){
      var   span = $('<span></span>') ;
      if(itemOptions.isSticky){
        span.addClass('sticky');
      }
      span.html(html);
      return $('<li></li>').append(span);
    };
  }
    
    
  function addItem(html, type, instId){
    var inst = getInstance(instId);
    
    
    
    var options;
    if($.isPlainObject(type)){
      options = type;
    }else{
      options = {};
      options.type = type;
    }
    
    options = $.extend({
      type: defaultType,
      isSticky: inst.options.sticky,
      delay: inst.options.delay
    }, options);
    
   
    inst.con.show();
   
    var item = inst.options.itemRender(html, options, inst);
    item.addClass(options.type);
    inst.body.append(item);

    $(item).data('ct_delay', options.delay);
    
    if(options.isSticky){
      inst.stickyItemCount++;
      $(item).data('ct_isSticky', true);
    }

      
    fixWidth(inst);
    doAlign(inst);  
   
  }

  function fixWidth(inst){
    var con = inst.con;
    
   
    if(inst.options.autoWidth == 'disabled'){
      
      if(inst.options.width != null){
        inst.con.width(inst.options.width);
      }
    }else if(inst.options.autoWidth == 'fit'){
      con.width(inst.options.appendTo.width() - (con.outerWidth() -  con.width()));
      

    }else if(inst.options.autoWidth == 'fitWindow'){
      con.width($(window).width() - (con.outerWidth() - con.width()));
    }
    
  }



  function removeItem(instId){
    if(instId == undefined){
      return;
    }
    var inst = getInstance(instId);
    var con = inst.con;
    var body = inst.body;
    var opt = inst.options;
    
    if(con == null){
      return;
    }

    var size = Math.max(body.children().size() - inst.stickyItemCount, 0);
    //console.log('removedItem: tota=', body.children().size() + ', sticky=' + inst.stickyItemCount + ', inst.id = ' + inst.id);
   
    
    if(size == 0){
      inst.inTimeout = false;
      inst.timerId = null;
      if(body.children().size() == 0){
        hide(inst);
      }else{
          
      }
      
      return;
    }else if(size > 0){
      inst.inTimeout = true;
      getInstance(instId).inTimeout = true;


      var firstRemovable = getRemovableItem(instId);
        
      if(firstRemovable != null){
        inst.timerId = setTimeout(function(){
          var savedInst = inst;
         
		 if(opt.animated){
            firstRemovable[opt.animateType](opt.animateSpeed, function(){
              doRemoveItem( inst.id, firstRemovable);
            });
          }else{
            doRemoveItem( inst.id, firstRemovable);
          }
        

        }, firstRemovable.data('ct_delay'));
      }
    }
  }
  

  function getRemovableItem(instId){
    
    //console.log('getRemovableItem', instId);
    var inst = getInstance(instId);
    var children = inst.body.children();
    
    for(var i=0; i< children.size(); i++){
      if( $(children[i]).data('ct_isSticky') != true){
        return $(children[i]);
      }
    }
    
    return null;
  
  }

  function doRemoveItem(instId, item){
    //console.log('doRemoveId called');
    item.remove();
    removeItem(instId);
  }
 
})(jQuery); 
