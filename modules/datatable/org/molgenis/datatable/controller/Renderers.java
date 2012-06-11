package org.molgenis.datatable.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.plugin.JQGridPlugin;
import org.molgenis.datatable.plugin.JQGridPlugin.JQGridResult;
import org.molgenis.datatable.view.AbstractExporter;
import org.molgenis.datatable.view.ExcelExporter;
import org.molgenis.framework.ui.html.HtmlWidget;

import com.google.gson.Gson;

/**
 * Class containing a series of simple renderers to do the administrative busywork required to render from a particular
 * {@link TupleTable} to a particular view (current options are an {@link AbstractExporter} or a {@link HtmlWidget}. See the 
 * org.molgenis.modules.datatable.view package.
 */
public class Renderers {

	public static class HeaderHelper {
		public static void setHeader(HttpServletResponse response, String contentType, String fileName) {
			response.setContentType(contentType);
			response.addHeader("Content-Disposition", "attachment; filename="+ fileName);
		}
	}
	
	/**
	 * Interface to render from a Table/request combination to a particular view. Current implementations are trivial
	 * except {@link SPSSRenderer}.
	 */
	public interface Renderer {
		public void export(HttpServletResponse response, String fileName, JQGridPlugin jqGridPlugin, TupleTable tupleTable, int totalPages, int currentPage) throws TableException, IOException;
	}

	public static class JQGridRenderer implements Renderer {
		@Override
		public void export(HttpServletResponse response, String fileName, JQGridPlugin jqGridPlugin, TupleTable tupleTable, int totalPages, int currentPage) throws TableException, IOException {
			final JQGridResult result = JQGridPlugin.buildJQGridResults(tupleTable.getRowCount(), totalPages, currentPage, tupleTable);
			final PrintWriter pout = new PrintWriter(response.getOutputStream());
			pout.print(new Gson().toJson(result));
			pout.close();
		}		
	}
	
	public static class ExcelRenderer implements Renderer {
		@Override
		public void export(HttpServletResponse response, String fileName, JQGridPlugin jqGridPlugin, TupleTable tupleTable, int totalPages, int currentPage) throws TableException, IOException {
			HeaderHelper.setHeader(response, "application/ms-excel", fileName + ".xlsx");
			final ExcelExporter excelExport = new ExcelExporter(tupleTable);
			excelExport.export(response.getOutputStream());		
		}
	}
	
	public static class CSVRenderer implements Renderer {
		@Override
		public void export(HttpServletResponse response, String fileName, JQGridPlugin jqGridPlugin, TupleTable tupleTable, int totalPages, int currentPage) throws TableException, IOException {
			HeaderHelper.setHeader(response, "application/ms-excel", fileName + ".csv");
			final ExcelExporter excelExport = new ExcelExporter(tupleTable);
			excelExport.export(response.getOutputStream());	
		}
	}	
	
	/**
	 * Several things need to happen to export to SPSS:
	 * <ul>
	 *  <li>Several files need to be created in the temp dir: 
	 *  <li>One textfile encoding the actual data, for example as tab-separated values.</li>
	 *  <li>One .sps syntax script that will set the variables and labels in SPSS and load the data.</li>
	 *  <li>One textfile with instructions on how to use the script in SPSS.</li>
	 *  <li>These files should be compressed together into a .zip file for download.</li>
	 * </ul>
	 */
	public static class SPSSRenderer implements Renderer {
		@Override
		public void export(HttpServletResponse response, String fileName, JQGridPlugin jqGridPlugin, TupleTable tupleTable, int totalPages, int currentPage) throws TableException, IOException {
//			try {
//				final File tempDir = (File)controller.getContext().getServletContext().getAttribute( "javax.servlet.context.tempdir" );
//				final File spssFile = File.createTempFile( "spssExport", ".sps", tempDir );
//				final File spssCsvFile = File.createTempFile( "csvSpssExport", ".csv", tempDir );
//				// TODO: instruction .txt file.
//				final File zipExport = File.createTempFile( "spssExport", ".zip", tempDir );
//				
//				final FileOutputStream spssFileStream = new FileOutputStream(spssFile);
//				final FileOutputStream spssCsvFileStream = new FileOutputStream(spssCsvFile);
//				final SPSSExporter spssExporter = new SPSSExporter(tupleTable);
//				spssExporter.export(spssCsvFileStream, spssFileStream, spssCsvFile.getName());
//				
//				spssCsvFileStream.close();
//				spssFileStream.close();
//				ZipUtils.compress(Arrays.asList(spssFile, spssCsvFile), zipExport, DirectoryStructure.EXCLUDE_DIR);
//				HeaderHelper.setHeader(response, "application/octet-stream", fileName + ".zip");
//				exportFile(zipExport, response);
//			} catch (Exception e) {
//				throw new TableException(e);
//			}		
		}
		
		private void exportFile(File file, HttpServletResponse response) throws IOException {
			FileInputStream fileIn = new FileInputStream(file);
			ServletOutputStream out = response.getOutputStream();
			 
			byte[] outputByte = new byte[4096];
			//copy binary content to output stream
			while(fileIn.read(outputByte, 0, 4096) != -1)
			{
				out.write(outputByte, 0, 4096);
			}
			fileIn.close();
			out.flush();
			out.close();			
		}
	}
}