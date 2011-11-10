package plugins.batch;

import java.util.List;

import org.molgenis.batch.MolgenisBatchEntity;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.Table;

public class BatchViewUi {
	private Container container = new Container();
	
	public void setContainer(Container container) {
		this.container = container;
	}

	public Container getContainer() {
		return container;
	}

	public void updateBatchView(BatchContainer batchContainer, BatchService service) {
		
		// Clear container
		container = new Container();
		
		// Set up table
		Table batchTable = new Table("BatchTable");
		batchTable.addColumn("Entity name");
		
		// Show the batches
		List<Batch> batches = batchContainer.getBatches();
		int row = 0;
		for (Batch b : batches) {
			List<MolgenisBatchEntity> entityList = b.getMolgenisBatchEntities();
			for (MolgenisBatchEntity entity : entityList) {
				batchTable.addRow(b.getName());
				batchTable.setCell(0, row, entity.getName());
				row++;
			}
			
			if (entityList.size() == 0) {
				row++;
			}
		}
		
		container.add(batchTable);
	}
}
