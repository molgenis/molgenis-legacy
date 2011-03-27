/* Date:        March 7, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringEscapeUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.TextParagraph;
import org.molgenis.util.Tuple;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PrintLabelPlugin extends GenericPlugin
{
	private static final long serialVersionUID = 8416302930361487397L;
	
	private Container container;
	private ActionInput printButton;
	private TextParagraph text;

	public PrintLabelPlugin(String name, ScreenModel parent)
	{
		super(name, parent);
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			String action = request.getString("__action");
			
			if( action.equals("Print") )
			{
				String filename = "hello.pdf";
				File pdfFile = createPdf(filename);
				servePdf(pdfFile);
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}

	private File createPdf(String filename) throws FileNotFoundException, DocumentException {
		File pdfFile = new File(filename);
		Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile.getAbsoluteFile()));
        document.open();
        document.add(new Paragraph("Hello World!"));
        document.close();
        return pdfFile;
	}
	
	private void servePdf(File pdfFile) {
		text = new TextParagraph("pdfFilename", "<a href=\"" + pdfFile.getAbsoluteFile() + "\">Download pdf</a>");
		container.add(text);
	}

	@Override
	public void reload(Database db)
	{
		if (container == null) {
			container = new Container();
			printButton = new ActionInput("Print", "", "Print");
			container.add(printButton);
		}
	}
	
	public String render() {
		return container.toHtml();
	}
	
	@Override
	public boolean isVisible()
	{
		if (this.getLogin().isAuthenticated()){
			return true;
		} else {
			return false;
		}
	}
}
