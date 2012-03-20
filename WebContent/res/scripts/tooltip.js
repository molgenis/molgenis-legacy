/*
 +-------------------------------------------------------------------+
 |                   J S - T O O L T I P   (v2.4)                    |
 |                                                                   |
 | Copyright Gerd Tentler               www.gerd-tentler.de/tools    |
 | Created: Feb. 15, 2005               Last modified: Aug. 20, 2011 |
 +-------------------------------------------------------------------+
 | This program may be used and hosted free of charge by anyone for  |
 | personal purpose as long as this copyright notice remains intact. |
 |                                                                   |
 | Obtain permission before selling the code for this program or     |
 | hosting this software on a commercial website or redistributing   |
 | this software over the Internet or in any other medium. In all    |
 | cases copyright must remain intact.                               |
 +-------------------------------------------------------------------+

======================================================================================================

 This script was tested with the following systems and browsers:

 - Windows: IE, Opera, Firefox, Chrome

 If you use another browser or system, this script may not work for you - sorry.

------------------------------------------------------------------------------------------------------

 USAGE:

 Use the toolTip-function with mouse-over and mouse-out events (see examples below).

 - To show a tooltip, use this syntax: toolTip(text, size, opacity, padding, border)
   o text: HTML content
   o size: width in pixels or "width:height", example: "270:100"
   o opacity: 1 - 100
   o padding: pixels
   o border: CSS rule, example: "1px solid red"
   Note: all arguments except for text are optional. Opacity is not supported by all browsers.

 - To hide a tooltip, use this syntax: toolTip()

------------------------------------------------------------------------------------------------------

 EXAMPLES:

 <span onMouseOver="toolTip('Just a <b>test</b>', 150)" onMouseOut="toolTip()">some text here</span>
 <a href="image.jpg" onMouseOver="toolTip('<img src=thumb.jpg>', '320:240', 100, 0, '')" onMouseOut="toolTip()">my image</a>

======================================================================================================
*/

var OP = (navigator.userAgent.indexOf('Opera') != -1);
var IE = (navigator.userAgent.indexOf('MSIE') != -1 && !OP);
var GK = (navigator.userAgent.indexOf('Gecko') != -1);
var SA = (navigator.userAgent.indexOf('Safari') != -1);

var tooltip = null;

function TOOLTIP() {
//----------------------------------------------------------------------------------------------------
// Configuration
//----------------------------------------------------------------------------------------------------
	this.width = 250;					// width (pixels)
	this.bgColor = "#BDCDDA";			// background color
	this.textFont = "Helvetica";		// text font family
	this.textSize = 11;					// text font size (pixels)
	this.textColor = "#000000";			// text color
	this.textAlign = "left";			// text alignment: "left", "right" or "center"
	this.border = "2px solid #000000";	// border (CSS spec: size style color, e.g. "1px solid #D00000")
	this.padding = 4;					// padding (pixels)
	this.opacity = 90;					// opacity (1 - 100); not supported by all browsers
	this.cursorDistance = 5;			// distance from mouse cursor (pixels)
	this.xPos = "right";				// horizontal position: "left" or "right"
	this.yPos = "bottom";				// vertical position: "top" or "bottom"

	// don't change
	this.text = '';
	this.height = 0;
	this.obj = null;
	this.active = false;

//----------------------------------------------------------------------------------------------------
// Methods
//----------------------------------------------------------------------------------------------------
	this.create = function() {
		if(!this.obj) this.init();

		this.obj.style.border = this.border;
		this.obj.style.padding = this.padding + 'px';
		if(this.width) this.obj.style.width = this.width + 'px';
		if(this.height) this.obj.style.height = this.height + 'px';
		if(this.textFont) this.obj.style.fontFamily = this.textFont;
		if(this.textSize) this.obj.style.fontSize = this.textSize + 'px';
		if(this.textColor) this.obj.style.color = this.textColor;
		if(this.textAlign) this.obj.style.textAlign = this.textAlign;
		if(this.bgColor) this.obj.style.backgroundColor = this.bgColor;

		this.obj.innerHTML = this.text;
		this.height = this.obj.offsetHeight;

		this.setOpacity();
		this.move();
		this.show();
	}

	this.init = function() {
		this.obj = document.getElementById('ToolTip');
		if(this.obj) document.body.removeChild(this.obj);
		this.obj = document.createElement('div');
		this.obj.id = 'ToolTip';
		this.obj.style.position = 'absolute';
		this.obj.style.visibility = 'hidden';
		this.obj.style.cursor = 'pointer';
		this.obj.style.boxSizing = 'border-box';
		this.obj.style.MozBoxSizing = 'border-box';
		this.obj.style.msBoxSizing = 'border-box';
		this.obj.style.webkitBoxSizing = 'border-box';
		this.obj.onmouseover = function() { tooltip.show(); }
		this.obj.onmouseout = function() { tooltip.hide(); }
		this.obj.onclick = function() { tooltip.hide(); }
		document.body.appendChild(this.obj);
	}

	this.move = function() {
		var winX = getWinX() - (((GK && !SA) || OP) ? 17 : 0);
		var winY = getWinY() - (((GK && !SA) || OP) ? 17 : 0);
		var x = mouseX;
		var y = mouseY;

		if(this.xPos == 'left') {
			if(x - this.width - this.cursorDistance < getScrX())
				x = getScrX();
			else x -= this.width + this.cursorDistance;
		}
		else {
			if(x + this.width + this.cursorDistance > winX + getScrX())
				x = winX + getScrX() - this.width;
			else x += this.cursorDistance;
		}

		if(this.yPos == 'top') {
			if(y - this.height - this.cursorDistance < getScrY())
				y = getScrY();
			else y -= this.height + this.cursorDistance;
		}
		else {
			if(y + this.height + this.cursorDistance > winY + getScrY())
				y = winY + getScrY() - this.height;
			else y += this.cursorDistance;
		}
		this.obj.style.left = x + 'px';
		this.obj.style.top = y + 'px';
	}

	this.show = function() {
		this.obj.style.zIndex = 69;
		this.active = true;
		this.obj.style.visibility = 'visible';
	}

	this.hide = function() {
		this.obj.style.zIndex = -1;
		this.active = false;
		this.obj.style.visibility = 'hidden';
	}

	this.setOpacity = function() {
		this.obj.style.opacity = this.opacity / 100;
		this.obj.style.MozOpacity = this.opacity / 100;
		this.obj.style.KhtmlOpacity = this.opacity / 100;
		this.obj.style.filter = 'alpha(opacity=' + this.opacity + ')';
	}
}

//----------------------------------------------------------------------------------------------------
// Global functions
//----------------------------------------------------------------------------------------------------
function getScrX() {
	var offset = 0;
	if(window.pageXOffset)
		offset = window.pageXOffset;
	else if(document.documentElement && document.documentElement.scrollLeft)
		offset = document.documentElement.scrollLeft;
	else if(document.body && document.body.scrollLeft)
		offset = document.body.scrollLeft;
	return offset;
}

function getScrY() {
	var offset = 0;
	if(window.pageYOffset)
		offset = window.pageYOffset;
	else if(document.documentElement && document.documentElement.scrollTop)
		offset = document.documentElement.scrollTop;
	else if(document.body && document.body.scrollTop)
		offset = document.body.scrollTop;
	return offset;
}

function getWinX() {
	var size = 0;
	if(window.innerWidth)
		size = window.innerWidth;
	else if(document.documentElement && document.documentElement.clientWidth)
		size = document.documentElement.clientWidth;
	else if(document.body && document.body.clientWidth)
		size = document.body.clientWidth;
	else size = screen.width;
	return size;
}

function getWinY() {
	var size = 0;
	if(window.innerHeight)
		size = window.innerHeight;
	else if(document.documentElement && document.documentElement.clientHeight)
		size = document.documentElement.clientHeight;
	else if(document.body && document.body.clientHeight)
		size = document.body.clientHeight;
	else size = screen.height;
	return size;
}

function getMouseXY(e) {
	if(e && e.pageX != null) {
		mouseX = e.pageX;
		mouseY = e.pageY;
	}
	else if(event && event.clientX != null) {
		mouseX = event.clientX + getScrX();
		mouseY = event.clientY + getScrY();
	}
	if(mouseX < 0) mouseX = 0;
	if(mouseY < 0) mouseY = 0;
	if(tooltip && tooltip.active) tooltip.move();
}

function toolTip(text, size, opacity, padding, border) {
	if(typeof text != 'undefined') {
		tooltip = new TOOLTIP();
		tooltip.text = text;
		if(size) {
			size = '' + size;
			var s = size.split(':');
			if(s[0]) tooltip.width = parseInt(s[0]);
			if(s[1]) tooltip.height = parseInt(s[1]);
		}
		if(opacity) tooltip.opacity = opacity;
		if(typeof padding != 'undefined') tooltip.padding = padding;
		if(typeof border != 'undefined') tooltip.border = border;
		tooltip.create();
	}
	else if(tooltip) tooltip.hide();
}

//----------------------------------------------------------------------------------------------------
// Event handlers
//----------------------------------------------------------------------------------------------------
var mouseX = mouseY = 0;
document.onmousemove = getMouseXY;

//----------------------------------------------------------------------------------------------------
