package qtltogff.sources;

public class GffEntry {

	//chrII	WormQTL	qtl_interval	1000000	1000500	500	.	.	AGIUSA0001
	
	private String chr;
	private String src;
	private String feat;
	private Long start;
	private Long stop;
	private Integer score;
	private String group;
	
	public GffEntry()
	{
		super();
	}
	
	public GffEntry(String chr, String src, String feat, Long start, Long stop,
			Integer score, String group) {
		super();
		this.chr = chr;
		this.src = src;
		this.feat = feat;
		this.start = start;
		this.stop = stop;
		this.score = score;
		this.group = group;
	}
	
	
	public String getChr() {
		return chr;
	}
	public void setChr(String chr) {
		this.chr = chr;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getFeat() {
		return feat;
	}
	public void setFeat(String feat) {
		this.feat = feat;
	}
	public Long getStart() {
		return start;
	}
	public void setStart(Long start) {
		this.start = start;
	}
	public Long getStop() {
		return stop;
	}
	public void setStop(Long stop) {
		this.stop = stop;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	
	
	
}
