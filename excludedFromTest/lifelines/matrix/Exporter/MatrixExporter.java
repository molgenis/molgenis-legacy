package lifelines.matrix.Exporter;

import java.io.IOException;
import java.io.OutputStream;

import lifelines.matrix.PagableMatrix;

public interface MatrixExporter {
	public void export(PagableMatrix matrix, OutputStream outputStream) throws IOException, Exception;
	public String getContentType();
	public String getFileExtenstion();
}
