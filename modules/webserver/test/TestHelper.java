package test;

import java.io.File;

import org.molgenis.framework.db.Database;
import org.molgenis.util.TarGz;

import app.servlet.MolgenisServlet;

public class TestHelper {

	public static void deleteDatabase() throws Exception {
		File dbDir = new File("hsqldb");
		if (dbDir.exists()) {
			TarGz.recursiveDeleteContentIgnoreSvn(dbDir);
		} else {
			throw new Exception("HSQL database directory does not exist");
		}

		if (dbDir.list().length != 1) {
			throw new Exception(
					"HSQL database directory does not contain 1 file (.svn) after deletion! it contains: " + dbDir.list().toString());
		}
	}

	public static void deleteStorage() throws Exception {
		// get storage folder and delete it completely
		// throws exceptions if anything goes wrong
		Database db = new MolgenisServlet().getDatabase();
		int appNameLength = MolgenisServlet.getMolgenisVariantID().length();
		String storagePath = db.getFileSourceHelper().getFilesource(true)
				.getAbsolutePath();
		File storageRoot = new File(storagePath.substring(0,
				storagePath.length() - appNameLength));
		System.out.println("Removing content of " + storageRoot);
		TarGz.recursiveDeleteContent(new File(storagePath));
		System.out.println("Removing folder " + storageRoot);
		TarGz.delete(storageRoot, true);
	}

	public static void main(String[] args) throws Exception {
		deleteDatabase();

	}

}
