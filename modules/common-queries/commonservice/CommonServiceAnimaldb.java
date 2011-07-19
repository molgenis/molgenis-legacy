package commonservice;



public class CommonServiceAnimaldb extends CommonService
{
	private CommonServiceAnimaldb() {
		super();
	}
	
	public static CommonService getInstance() {
		if (instance == null) {
			instance = new CommonServiceAnimaldb();
		}
		return (CommonService) instance;
	}
}
