/* http://www.quirksmode.org/js/events_properties.html
TODO: use generic targets and events
var targ;
	if (!e) var e = window.event;
	if (e.target) targ = e.target;
	else if (e.srcElement) targ = e.srcElement;
	if (targ.nodeType == 3) // defeat Safari bug
		targ = targ.parentNode;
*/


//idea: use http://www.svendtofte.com/code/date_format/formatDate.js for generic date formatting
function showDateInput(input)
{
	showDateInput(input,false);
}

function showDateInput(input, isDatetime)
{	
	var id = input.name + "_"+input.form.name;
	
	//there can be only one so check global dateInput variable
	if(window.dateInputDiv &&  window.dateInputDiv.id != id)
		window.dateInputDiv.style.display = "none"; 
	
	if(document.getElementById(id) == null)
	{
		var myInput = new DateInput(input, isDatetime);
		myInput.isDatetime = isDatetime;
		window.dateInputDiv = myInput.calendar;
	}
	else
	{
		window.dateInputDiv = document.getElementById(id);
		if(window.dateInputDiv.style.display == "block")
			window.dateInputDiv.style.display= "none";	
		else
			window.dateInputDiv.style.display = "block";	
	}	
}
/**constructor, to pass parameters*/
function DateInput(input, isDatetime)
{
	this.init(input, isDatetime);
}

DateInput.prototype = {
	init: function(input, isDatetime)
	{	
		this.isDatetime = isDatetime;
		
		//get the date
		this.input = input;
		this.date = new Date(input.value);
		if( !parseInt(this.date.getYear()) )
		{
			this.date = new Date();
		}
		
		//constants
		this.days = ['S','M','T','W','T','F','S'];		
		this.months = ['January','February','March','April','May','June','July','August','September','October','November','December'];	
				
		//set current values and selection values (used during selection)
  		this.currentYear = this.selectedYear = this.date.getFullYear();
  		this.currentMonth = this.selectedMonth = this.date.getMonth() + 1; //!
  		this.currentDay = this.selectedDay = this.date.getDate();	
		this.currentHours = this.selectedHours = this.date.getHours();
		this.currentMinutes = this.selectedMinutes = this.date.getMinutes();
		this.currentSeconds = this.selectedSeconds = this.date.getSeconds();  							
		
		//create the calendar
		this.calendar = document.createElement("div");
		this.calendar.className = "dateinput";
		this.calendar.style.display = "block";
		this.calendar.id = input.name + "_"+input.form.name;	
		this.input.parentNode.insertBefore(this.calendar, this.input.nextSibling);
		this.drawCalendar();
		this.calendar.setAttribute("DateInputObject", this);
		
		
		//ensure that it shows the right day when input gets focus (may be changed by hand)
		var _this = this;
		this.addEvent(input, "click", function(e)
		{			
			var date = new Date(_this.input.value);
			if(parseInt(date.getYear()))			
			{
				_this.date = date;
  				_this.currentYear = _this.selectedYear = _this.date.getFullYear();
  				_this.currentMonth = _this.selectedMonth = _this.date.getMonth() + 1; //!
  				_this.currentDay = _this.selectedDay = _this.date.getDate();	
  				
				_this.currentHours = _this.selectedHours = _this.date.getHours();
				_this.currentMinutes = _this.selectedMinutes = _this.date.getMinutes();
				_this.currentSeconds = _this.selectedSeconds = _this.date.getSeconds();
  				
  				_this.label_month.nodeValue = _this.months[_this.selectedMonth-1] + " " + _this.selectedYear;				
			}
			_this.changeDateInputDays();
		});
	},
	drawCalendar: function()
	{
		var _this = this;
		
		//make table
		this.table = document.createElement("table");
		this.calendar.appendChild(this.table);
		
		//make a tbody
		this.tbody = document.createElement("tbody");
		this.table.appendChild(this.tbody);
		
		//row 1
		var row1 = document.createElement("tr");
		this.tbody.appendChild(row1);
		var td1_1 = document.createElement("th");		
		td1_1.className = "dateinput";			
		row1.appendChild(td1_1);
		var action_prev = document.createTextNode("<");
		td1_1.appendChild(action_prev);
		this.addEvent(td1_1, "click", function(e)
		{
			_this.changeDateInputMonth(-1);
			//alert("pref");
		});		
		
		var td1_2 = document.createElement("th");	
		td1_2.colSpan = 5;
		td1_2.className = "dateinput";	
		row1.appendChild(td1_2);
		this.label_month = document.createTextNode(this.months[this.selectedMonth-1] + " " + this.selectedYear);
		td1_2.appendChild(this.label_month);		
		
		var td1_3 = document.createElement("th");				
		td1_3.className = "dateinput";	
		row1.appendChild(td1_3);
		var action_next = document.createTextNode(">");	
		td1_3.appendChild(action_next);	
		this.addEvent(td1_3, "click", function(e)
		{
			_this.changeDateInputMonth(1);
			//alert("next");
		});			
		
		//row 2
		var row2 = document.createElement("tr");
		this.tbody.appendChild(row2);		
		for(day in this.days)
		{
			var td = document.createElement("td");	
			td.className="dateinput";	
			row2.appendChild(td);	
			var value = document.createTextNode(this.days[day]);			
			td.appendChild(value);
		}		
				
		//row 3 - row 3 + 6
		this.dayInputs = new Array(7*6);
		var day = 1;
		for(i = 0; i < 6; i++)
		{
			var row = document.createElement("tr");	
			this.tbody.appendChild(row);
			
			for(j = 0; j < 7; j++)
			{
				var td = document.createElement("td");
				td.className="dateinput";
				row.appendChild(td);	
				var value = document.createTextNode(day++);		
				td.appendChild(value);
							
				this.dayInputs[i*7 + j] = value;
				
				var val = j;
				this.addEvent(td, "click", function(e)
				{					
					//if empty day square: close without changing anything
					if(e.target)
					{
						if( e.target.childNodes[0].nodeValue == "")
						{
							_this.hideDataInput();
							_this.input.focus();
							return;
						}
					}
					else if( e.srcElement.childNodes[0].nodeValue == "")
					{
						_this.hideDataInput();
						_this.input.focus();
						return;
					}
					
					if (e.target)
					{
						_this.selectedDay = e.target.childNodes[0].nodeValue;
					}
					else
					{
						_this.selectedDay = e.srcElement.childNodes[0].nodeValue;
					}
					_this.currentMonth = _this.selectedMonth;
					_this.currentYear = _this.selectedYear;
					_this.currentDay = _this.selectedDay;
					
					if(_this.isDatetime)
					{
						_this.currentHours = _this.selectedHours;
						_this.currentMinutes = _this.selectedMinutes;
						_this.currentSeconds = _this.selectedSeconds;
						_this.input.value =  _this.months[_this.currentMonth-1]+" "+_this.currentDay+", "+_this.currentYear+", "+_this.twoDecimal(_this.currentHours)+":"+_this.twoDecimal(_this.currentMinutes)+":"+_this.twoDecimal(_this.currentSeconds);
					}
					else
					{
						_this.input.value =  _this.months[_this.currentMonth-1]+" "+_this.currentDay+", "+_this.currentYear;
					}
					_this.hideDataInput();
					_this.changeDateInputDays();
					_this.input.focus();
				});		
				this.addEvent(td, "mouseover",function(e)
				{
					if (e.target)
					{
						_this.selectedDay = e.target.style.background = "#999";
					}
					else
					{
						_this.selectedDay = e.srcElement.style.background = "#999";
					}					
				});		
				this.addEvent(td, "mouseout",function(e)
				{
					if (e.target)
					{
						_this.selectedDay = e.target.style.background = "white";
					}
					else
					{
						_this.selectedDay = e.srcElement.style.background = "white";
					}					
				});							
			}
		}
		//build time inputs
		if(this.isDatetime)
		{
			var row11 = document.createElement("tr");
			this.tbody.appendChild(row11);
			var td11_1 = document.createElement("td");	
			td11_1.colSpan = 7;	
			td11_1.className="dateinput";
			row11.appendChild(td11_1);
			var timelabel = document.createTextNode("time:");
			td11_1.appendChild(timelabel);
			//var timeinput = document.createElement("input");
			//timeinput.value =  _this.twoDecimal(_this.selectedHours) +":"+_this.twoDecimal(_this.selectedMinutes)+":"+_this.twoDecimal(_this.selectedSeconds);
			
			var hoursInput = document.createElement("input");
			hoursInput.value = _this.twoDecimal(_this.selectedHours);
			
			var minutesInput = document.createElement("input");
			minutesInput.value = _this.twoDecimal(_this.selectedMinutes);
			
			var secondsInput = document.createElement("input");
			secondsInput = _this.twoDecimal(_this.selectedSeconds);
			
			this.addEvent(timeinput , "change", function(e)
			{
				var values = timeinput.value.split(":");
				if(values.length != 3)
				{
					alert("date should be in the format hh:mm:ss");
					return false;
				}
				
				var hours = parseInt(values[0]);
				if( isNaN(hours) || hours < 0 || hours > 23 )
				{
					alert("hours should be in 0..23. Time is not changed.");
					timeinput.value =  _this.twoDecimal(_this.selectedHours) +":"+_this.twoDecimal(_this.selectedMinutes)+":"+_this.twoDecimal(_this.selectedSeconds);
					return false;
				}		
				var minutes = parseInt(values[1]);
				if( isNaN(minutes) || minutes < 0 || minutes > 59 )
				{
					alert("minutes should be in 0..59. Time is not changed.");
					timeinput.value =  _this.twoDecimal(_this.selectedHours) +":"+_this.twoDecimal(_this.selectedMinutes)+":"+_this.twoDecimal(_this.selectedSeconds);
					timeinput.focus();
					return false;
				}
				var seconds = parseInt(values[2]);
				if( isNaN(seconds) || seconds < 0 || seconds > 59 )
				{
					alert("seconds should be in 0..59. Time is not changed.");
					timeinput.value =  _this.twoDecimal(_this.selectedHours) +":"+_this.twoDecimal(_this.selectedMinutes)+":"+_this.twoDecimal(_this.selectedSeconds);
					timeinput.focus();
					return false;
				}			
				
				_this.selectedHours = hours;
				_this.selectedMinutes = minutes;
				_this.selectedSeconds = seconds;
			});	
			
			td11_1.appendChild(timeinput);
		}
		
		//build close button
		var row10 = document.createElement("tr");
		this.tbody.appendChild(row10);
		var td10_1 = document.createElement("td");
		td10_1.colSpan = 7;
		td10_1.className="dateinput";
		row10.appendChild(td10_1);
		var action_close = document.createTextNode("close");
		td10_1.appendChild(action_close);
		this.addEvent(td10_1 , "click", function(e)
		{
			_this.hideDataInput();
			
		});	
		td10_1.appendChild(action_close);	
		
		this.changeDateInputDays();								
	},
	twoDecimal: function(value)
	{
		var value = String(value);
		return (value.length == 1 ? "0"+value : value ); 
	},
	changeDateInputDays:function()
	{
		var startdate = new Date();
		startdate.setYear(this.selectedYear);
		startdate.setMonth(this.selectedMonth - 1);
		startdate.setDate(1);

		var weekday = startdate.getDay();
		var day = 1;
		var outofrange = false;
		for(i in this.dayInputs)
		{
			if(i>=weekday && !outofrange && startdate.setDate(day))
			{
				weekday = 0;
				if(startdate.getDate() != day) 
				{
					outofrange = true;
					this.dayInputs[i].nodeValue = "";
				}
				else
				{
					if(day == this.currentDay && this.selectedYear == this.currentYear && this.currentMonth == this.selectedMonth)
					{
						this.dayInputs[i].parentNode.style.color = "red";
						this.dayInputs[i].parentNode.style.border = "solid red 1px";
					}
					else
					{
						this.dayInputs[i].parentNode.style.color = "black";
						this.dayInputs[i].parentNode.style.border = "solid white 1px";
					}
					this.dayInputs[i].nodeValue = day++;				
				}
			}
			else
			{
				this.dayInputs[i].nodeValue = "";
			}
		}
		
	},
	changeDateInputMonth: function(change)
	{
    	this.selectedMonth += change;
    	this.selectedDay = 0;
    	if(this.selectedMonth > 12) 
    	{
      		this.selectedMonth = 1;
      		this.selectedYear++;
    	} 
    	else if(this.selectedMonth < 1) 
    	{
     	 	this.selectedMonth = 12;
     		this.selectedYear--;
    	}

	   this.label_month.nodeValue = this.months[this.selectedMonth-1] + " " + this.selectedYear;	
	   this.changeDateInputDays();		   
	},
	hideDataInput: function()
	{
		this.calendar.style.display = "none";
	},
	addEvent : function(obj, eventname, func)
	{
		if (navigator.userAgent.match(/MSIE/)) 
		{	
			obj.attachEvent("on"+eventname, func);
		}
		else
		{	
			obj.addEventListener(eventname, func, true);
		}
	}	
}