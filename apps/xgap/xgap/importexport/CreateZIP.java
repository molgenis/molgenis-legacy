package xgap.importexport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CreateZIP {

//	public CreateZIP(List<File> files, File outputZIP) throws IOException {
//		zipThis(files, outputZIP);
//	}

	public static void ZipThis2(List<File> files, File outputZIP) throws IOException {

		final int BUFFER = 2048;

		BufferedInputStream origin = null;
		FileOutputStream dest = new FileOutputStream(outputZIP.getAbsolutePath());
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
		// out.setMethod(ZipOutputStream.DEFLATED);
		byte data[] = new byte[BUFFER];
		// get a list of files from current directory


		for (File f : files) {
			FileInputStream fi = new FileInputStream(f);
			origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry(f.getAbsolutePath());
			out.putNextEntry(entry);
			int count;
			while ((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();
		}
		out.close();

	}

	public static void zipThis(List<File> files, File outputZIP) throws IOException {

		// Create a buffer for reading the files
		byte[] buf = new byte[1024];

		// Create the ZIP file
		String outFilename = "outfile.zip";
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

		// Compress the files
		// for (int i=0; i<filenames.length; i++) {
		for (File q : files) {
			FileInputStream in = new FileInputStream(q.getAbsolutePath());

			// Add ZIP entry to output stream.
			out.putNextEntry(new ZipEntry(q.getAbsolutePath()));

			// Transfer bytes from the file to the ZIP file
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			// Complete the entry
			out.closeEntry();
			in.close();
		}

		// Complete the ZIP file
		out.close();

	}

}
