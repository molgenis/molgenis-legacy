package org.molgenis.animaldb.plugins.administration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Paragraph;
import com.itextpdf.text.*;

//import com.itextpdf.text.Chunk;
import com.itextpdf.text.pdf.*;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPRow;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;

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
    	PdfPTable elementTable = new PdfPTable(5);
    	//elementTable.
    	int elementCtr = 0;
    	Font valueFont = new Font(FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD, new BaseColor(0, 0, 0)));
    	Font headerFont = new Font(FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new BaseColor(0, 0, 0)));
    	for (String header : elementHeaderList) {
    		    		
    		PdfPCell headerCell = new PdfPCell();
    		Chunk headerChunk = new Chunk(header);
    		headerChunk.setFont(headerFont);
    		headerCell.addElement(headerChunk);
    		headerCell.setColspan(2);
    		headerCell.setBorderWidthRight(0);
    		headerCell.setPadding(2);
    		
    		elementTable.addCell(headerCell);
    		
    		PdfPCell valueCell = new PdfPCell();
    		valueCell.setColspan(3);
    		String value = elementList.get(elementCtr++);
    		if (value == null)
    		{
    			value = "";
    		}
    		
    		Chunk valueChunk = new Chunk("" + value);
    		valueChunk.setFont(valueFont);
    		valueCell.addElement(valueChunk);
    		valueCell.setBorderWidthLeft(0);
    		valueCell.setPadding(2);
    		elementTable.addCell(valueCell);
    	}
		elementTable.setWidthPercentage(100);
		labelCell.setPadding(20);
		labelCell.setBorderWidth(1);
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
