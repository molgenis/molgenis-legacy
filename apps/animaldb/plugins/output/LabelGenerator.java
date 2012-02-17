package plugins.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
//import com.itextpdf.text.Chunk;
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
	
	public void startDocument(File pdfFile) throws LabelGeneratorException {
		document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new LabelGeneratorException();
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new LabelGeneratorException();
		}
        document.open();
        table = new PdfPTable(nrOfColumns);
	}
	
	public void finishDocument() throws LabelGeneratorException {
		try {
			document.add(table);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new LabelGeneratorException();
		}
        document.close();
	}
	
	/**
	 * Add a two-column label to the document: first column contains the headers,
	 * second the values.
	 * 
	 * @param elementHeaderList
	 * @param elementList
	 */
	public void addLabelToDocument(List<String> elementHeaderList, List<String> elementList) {
		
    	PdfPCell labelCell = new PdfPCell();
    	PdfPTable elementTable = new PdfPTable(2);
    	int elementCtr = 0;
    	for (String header : elementHeaderList) {
    		Phrase headerPhrase = new Phrase(header);
    		headerPhrase.setFont(new Font(Font.FontFamily.HELVETICA, 6));
    		elementTable.addCell(headerPhrase);
    		Phrase valuePhrase = new Phrase(elementList.get(elementCtr++));
    		valuePhrase.setFont(new Font(Font.FontFamily.HELVETICA, 6));
    		elementTable.addCell(valuePhrase);
    	}
		elementTable.setWidthPercentage(100);
		labelCell.setPadding(0);
		labelCell.setBorderWidth(2);
    	labelCell.addElement(elementTable);
    	table.addCell(labelCell);
	}
	
	
	/**
	 * Add a one-column label to the document with only the values.
	 * 
	 * @param elementHeaderList
	 * @param elementList
	 */
	public void addLabelToDocument(List<String> elementList) {
		
    	PdfPCell labelCell = new PdfPCell();
    	for (String line : elementList) {
    		labelCell.addElement(new Paragraph(line, new Font(Font.FontFamily.HELVETICA, 6)));
    	}
		labelCell.setPadding(1);
		labelCell.setBorderWidth(1);
    	table.addCell(labelCell);
	}
	
}
