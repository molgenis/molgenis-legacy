package lifelines.loaders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatumTest {
	
	public static void main(String[] args) throws ParseException {
		String d = "2006/11/29 00:00:00";
		SimpleDateFormat format = new SimpleDateFormat("y/M/d H:m:s");
		Date da = format.parse(d);
		System.out.println(da.toString());
	}
	
	
}