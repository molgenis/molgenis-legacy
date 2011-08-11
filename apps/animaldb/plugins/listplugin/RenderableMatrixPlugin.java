/* Date:        February 24, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.listplugin;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.matrix.component.MatrixRenderer;
import org.molgenis.matrix.component.PhenoMatrix;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class RenderableMatrixPlugin extends PluginModel<Entity> {
	
	private static final long serialVersionUID = -8306984451248484959L;
	private PhenoMatrix matrix = null;
	private MatrixRenderer matrixRenderer = null;
	
	public RenderableMatrixPlugin(String name, ScreenController<?> parent) {
		super(name, parent);
	}
	
	@Override
	public String getViewName() {
		return "plugins_listplugin_RenderableMatrixPlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/listplugin/RenderableMatrixPlugin.ftl";
	}
	
	public MatrixRenderer getMatrixRenderer() {
		return this.matrixRenderer;
	}
	
	public void handleRequest(Database db, Tuple request) {
		
		try {
			String action = request.getString("__action");
			if (action.startsWith("matrix_component_request_tag_")) {
				matrixRenderer.delegateHandleRequest(request, matrix);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.setMessages(new ScreenMessage(e.getMessage(), false));
			}
		}
	}
	
	public void reload(Database db) {
		
		//if (matrix == null) {
			try {
				matrix = new PhenoMatrix(db, this.getViewName());
				matrixRenderer = new MatrixRenderer("Pheno Matrix", matrix, matrix);
			} catch (Exception e) {
				e.printStackTrace();
				if (e.getMessage() != null) {
					this.setMessages(new ScreenMessage(e.getMessage(), false));
				}
			}
		//}
	}

}
