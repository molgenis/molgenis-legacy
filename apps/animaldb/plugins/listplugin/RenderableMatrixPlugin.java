/* Date:        February 24, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.listplugin;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.matrix.component.MatrixRenderer;
import org.molgenis.matrix.component.PhenoMatrix;
import org.molgenis.matrix.component.general.MatrixRendererHelper;
import org.molgenis.matrix.component.interfaces.RenderableMatrix;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class RenderableMatrixPlugin extends PluginModel<Entity> {
	
	private static final long serialVersionUID = -8306984451248484959L;
	private PhenoMatrix matrix = null;
	private MatrixRenderer<ObservationTarget, ObservableFeature, List<ObservedValue>> matrixRenderer = null;
	private List<ObservationTarget> selectedTargetList = null;
	
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
	
	public MatrixRenderer<ObservationTarget, ObservableFeature, List<ObservedValue>> getMatrixRenderer() {
		return this.matrixRenderer;
	}
	
	public void handleRequest(Database db, Tuple request) {
		
		try {
			String action = request.getString("__action");
			
			if (action.startsWith(MatrixRendererHelper.MATRIX_COMPONENT_REQUEST_PREFIX)) {
				matrixRenderer.delegateHandleRequest(request);
			}
			
			if (action.equals("Save")) {
				RenderableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> currentMatrixSlice = 
						matrixRenderer.getRendered();
				this.setSelectedTargetList(currentMatrixSlice.getVisibleRows());
			}
			
			if (action.equals("resetMatrixRenderer")) {
				selectedTargetList = null;
				matrix = new PhenoMatrix(db);
				matrixRenderer = new MatrixRenderer<ObservationTarget, ObservableFeature, List<ObservedValue>>("Pheno Matrix", matrix, matrix, this.getName());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.setMessages(new ScreenMessage(e.getMessage(), false));
			}
		}
	}
	
	public void reload(Database db) {
		
		if (matrix == null) {
			try {
				matrix = new PhenoMatrix(db);
				matrixRenderer = new MatrixRenderer<ObservationTarget, ObservableFeature, List<ObservedValue>>("Pheno Matrix", matrix, matrix, this.getName());
			} catch (Exception e) {
				e.printStackTrace();
				if (e.getMessage() != null) {
					this.setMessages(new ScreenMessage(e.getMessage(), false));
				}
			}
		}
	}

	public List<ObservationTarget> getSelectedTargetList() {
		return selectedTargetList;
	}

	public void setSelectedTargetList(List<ObservationTarget> selectedTargetList) {
		this.selectedTargetList = selectedTargetList;
	}

}
