package lifelines.matrix.Exporter;

public class ExportFactory {
	
	public enum exportType {
		EXCEL,
		SPSS
	}
	
	public static MatrixExporter create(String type) {
		if(type.equalsIgnoreCase("Excel")) {
			return ExportFactory.create(exportType.EXCEL);
		} else if(type.equalsIgnoreCase("Spss")) {
			return ExportFactory.create(exportType.SPSS);
		}
		return null;
	}
	
	public static MatrixExporter create(exportType type) {
		if(exportType.EXCEL == type) {
			return new ExcelExporter();
		} else if (exportType.SPSS == type) {
			return new SpssExporter();
		}
		return null;		
	}
}