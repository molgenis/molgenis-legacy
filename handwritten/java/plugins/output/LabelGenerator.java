package plugins.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class LabelGenerator {
	
	private Document document;
	private PdfPTable table;
	
	private int nrOfColumns;
	
	public LabelGenerator(int nrOfColumns) {
		this.nrOfColumns = nrOfColumns;
	}
	
	public void startDocument(File pdfFile) throws FileNotFoundException, DocumentException {
		document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();
        table = new PdfPTable(nrOfColumns);
	}
	
	public void finishDocument() throws DocumentException {
		document.add(table);
        document.close();
	}
	
	public void addLabelToDocument(List<String> elementList) {
		
    	PdfPCell newCell = new PdfPCell();
    	
    	for (String line : elementList) {
    		newCell.addElement(new Paragraph(line));
    	}
    	
    	table.addCell(newCell);
	}
}
