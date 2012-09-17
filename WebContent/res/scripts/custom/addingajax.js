// create XMLHttpRequest object
function aaGetXmlHttpRequest() {

   var xmlHttpObj;
   if (window.XMLHttpRequest) {
      xmlHttpObj = new XMLHttpRequest();
   } else {
      try
         {
            xmlHttpObj = new ActiveXObject("Msxml2.XMLHTTP");
         }
         catch (e)
         {
            try
            {
               xmlHttpObj = new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch (e)
            {
               xmlHttpObj = false;
            }
         }
    }
   return xmlHttpObj;
}

// screen out older IE versions
function aaScreenIE() {
   if (navigator.appName == 'Microsoft Internet Explorer') {
     msie=navigator.appVersion.split("MSIE")
     version=parseFloat(msie[1]);
     if (version >= 6) return false;
   } else {
        return false;
   }
   return true;
}

// get stylesheet style
function aaGetStyle(obj, styleName) {
   if (obj.currentStyle) 
      return obj.currentStyle[styleName];
   else if (document.defaultView && document.defaultView.getComputedStyle) 
      return document.defaultView.getComputedStyle(obj,null).getPropertyValue(styleName);
   return undefined;
}  

// add event listening
function aaManageEvent(eventObj, event, eventHandler) {
   if (eventObj.addEventListener) {
      eventObj.addEventListener(event, eventHandler,false);
   } else if (eventObj.attachEvent) {
      event = "on" + event;
      eventObj.attachEvent(event, eventHandler);
   }
}

// cancel event
function aaCancelEvent(event) {
   if (event.preventDefault) {
      event.preventDefault();
      event.stopPropagation();
   } else {
      event.returnValue = false;
      event.cancelBubble = true;
   }
}

// stop event listening
function aaStopEvent(eventObj,event,eventHandler) {
   if (eventObj.removeEventListener) {
      eventObj.removeEventListener(event,eventHandler,false);
   } else if (eventObj.detachEvent) {
      event = "on" + event;
      eventObj.detachEvent(event,getKey);
   }
}

// return element
function aaElem(identifier) {
   return document.getElementById(identifier);
}

// add script
function aaAddScript(url) {
   var script = document.createElement('script');
   script.type = 'text/javascript';
   script.src = url;
   document.getElementsByTagName('head')[0].appendChild(script);
 }

function aaBindEventListener(obj, method) {
  return function(event) { method.call(obj, event || window.event)};
}

function aaBindObjMethod(obj, method) {
   return function() { method.apply(obj, arguments); }
}

function externalLinks() {
 var anchors = document.getElementsByTagName("a");
 for (var i=0; i<anchors.length; i++) {
   var anchor = anchors[i];
   if (anchor.getAttribute("href") &&
       anchor.getAttribute("rel") == "external")
     anchor.target = "_blank";
 }
}

aaManageEvent(window,'load',externalLinks);
