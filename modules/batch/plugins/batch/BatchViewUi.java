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
		batchTable.addColumn("Batch name");
		batchTable.addColumn("Entity name");
		
		// Show the batches
		List<Batch> batches = batchContainer.getBatches();
		int row = 0;
		for (Batch b : batches) {
			// Batch name
			batchTable.addRow("");
			batchTable.setCell(0, row, b.getName());
			
			// Batch entities
			int startRow = row;
			List<MolgenisBatchEntity> entityList = b.getMolgenisBatchEntities();
			for (MolgenisBatchEntity entity : entityList) {
				if (row > startRow) {
					batchTable.addRow("");
				}
				
				batchTable.setCell(1, row, entity.getName());
				
				row++;
			}
			
			if (entityList.size() == 0) {
				row++;
			}
		}
		
		container.add(batchTable);
	}
}
