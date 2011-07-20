package convertors.dbgap.jaxb.var_report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Stat
{
	@XmlAttribute(name="n")
	public String n;

	@XmlAttribute(name="nulls")
	public String nulls;

	@XmlAttribute(name="invalid_values")
	public String invalid_values;

	@XmlAttribute(name="special_values")
	public String special_values;

	@XmlAttribute(name="mean")
	public String mean;

	@XmlAttribute(name="mean_count")
	public String mean_count;

	@XmlAttribute(name="sd")
	public String sd;

	@XmlAttribute(name="median")
	public String median;

	@XmlAttribute(name="median_count")
	public String median_count;

	@XmlAttribute(name="min")
	public String min;

	@XmlAttribute(name="min_count")
	public String min_count;

	@XmlAttribute(name="max")
	public String max;

	@XmlAttribute(name="max_count")
	public String max_count;
	
	public String toString()
	{
		return String.format("Stat(n=%s, nulls=%s, invalid_values=%s, special_values=%s, mean=%s, mean_count=%s, sd=%s, median=%s, median_count=%s, min=%s, min_count=%s, max=%s, max_count=%s)",
				n, nulls, invalid_values, special_values, mean, mean_count, sd, median, median_count, min, min_count, max, max_count);
	}
}
