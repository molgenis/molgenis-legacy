package plugins.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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
	
	public void addLabelToDocument(List<String> elementLabelList, List<String> elementList) {
		
    	//create new label as a tablecell in the document
		PdfPCell labelCell = new PdfPCell();
		
		//PdfPCell newElementCell = new PdfPCell();
		//PdfPCell newElementLabelCell = new PdfPCell();
    	PdfPTable elementTable = new PdfPTable(2);
    	
        	
    	int elementCtr = 0;
    	for (String labelLine : elementLabelList) {
    		//Chunk blaat = new Chunk();
    		elementTable.addCell(new Phrase(labelLine));
    		elementTable.addCell(new Phrase(elementList.get(elementCtr)));
    		//newElementLabelCell.addElement(new Paragraph(labelLine));
    		elementCtr++;
    	//for (String line : elementList) {
    		//labelCell.addElement(new Paragraph(line));
    		//labelCell.addElement(new Paragraph(line));
    		//labelCell.setPadding(0);
    		//labelTable.addCell(labelCell);
    	//}
    	//newCell.addElement(labelTable);
    	}
    	//elementTable.addCell(newElementLabelCell);
		//elementTable.addCell(newElementCell);
		elementTable.setWidthPercentage(100);
		labelCell.setPadding(0);
		labelCell.setBorderWidth(2);
    	labelCell.addElement(elementTable);
    	table.addCell(labelCell);
	}
}






















