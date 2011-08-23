var submenu = null;
var closeTimer= null;

function mopen(subMenuId)
{
    if(submenu)
    {
    	if(submenu != document.getElementById(subMenuId))
    	{
			submenu.style.display='none';
			submenu = null;
			submenu = document.getElementById(subMenuId);
			submenu.style.display='block';
		}
		else
		{
			submenu.style.display='none';
			submenu = null;		
		}
	}
	else
	{
			submenu = document.getElementById(subMenuId);
			submenu.style.display='block';	
	}
}

function display(action, id)
{
	if (action == 'show')
	{
	document.getElementById(id).style.display = "block";
	}

	if (action == 'hide')
	{
	document.getElementById(id).style.display = "none";
	}
}