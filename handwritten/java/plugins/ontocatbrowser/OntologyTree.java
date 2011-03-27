package plugins.ontocatbrowser;

import org.molgenis.util.SimpleTree;

public class OntologyTree extends SimpleTree<OntologyTree> {
	private static final long serialVersionUID = 1L;

	public OntologyTree(String name, OntologyTree parent) {
		super(name, parent);
	}

	public String toString() {
		return this.getName();
	}

	public String getPath(String separator) {
		if (this.getParent() != null
				&& !this.getParent().getName().equals(
						OntocatBrowserPlugin.seper
								+ OntocatBrowserPlugin.rootName))
			return this.getParent().getPath(separator) + separator
					+ this.getName().split(OntocatBrowserPlugin.seper)[0];
		return this.getName().split(OntocatBrowserPlugin.seper)[0];
	}
}
