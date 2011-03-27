package plugins.breedingplugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Litter {
	private int id;
	private String name;
	private Date birthDate;
	private Date weanDate;
	private int size;
	private String isSizeApproximate;
	private int weanSize;
	private SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setBirthDate(String birthDate) throws ParseException {
		if (birthDate != null) {
			this.birthDate = sdf.parse(birthDate);
		} else {
			this.birthDate = null;
		}
	}
	public String getBirthDate() {
		if (birthDate == null) return "";
		return sdf.format(birthDate);
	}
	public void setWeanDate(String weanDate) throws ParseException {
		this.weanDate = sdf.parse(weanDate);
	}
	public String getWeanDate() {
		if (weanDate == null) return "";
		return sdf.format(weanDate);
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getSize() {
		if (size == -1) return "";
		return Integer.toString(size);
	}
	public void setSizeApproximate(String isSizeApproximate) {
		this.isSizeApproximate = isSizeApproximate;
	}
	public String getIsSizeApproximate() {
		return isSizeApproximate;
	}
	public void setWeanSize(int weanSize) {
		this.weanSize = weanSize;
	}
	public int getWeanSize() {
		return weanSize;
	}
}
