package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
	public static void deleteDirectoryRecursively(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.list(path).forEach(file -> {
				try {
					deleteDirectoryRecursively(file);
				} catch (IOException e) {
					throw new RuntimeException("Failed to delete file: " + file, e);
				}
			});
		}
		Files.deleteIfExists(path);
	}

	public static void copyDirectoryRecursively(Path source, Path destination) throws IOException {
		if (!Files.isDirectory(source)) {
			Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
		} else {
			if (!Files.exists(destination)) {
				Files.createDirectories(destination);
			}

			try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(source)) {
				for (Path path : directoryStream) {
					copyDirectoryRecursively(path, destination.resolve(source.relativize(path)));
				}
			}
		}
	}

	public static void unzip(File zipFile, File destDir) throws IOException {
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			File newFile = newFile(destDir, zipEntry);
			if (zipEntry.isDirectory()) {
				if (!newFile.isDirectory() && !newFile.mkdirs()) {
					throw new IOException("Failed to create directory " + newFile);
				}
			} else {
				// Make sure parent directory exists
				File parent = newFile.getParentFile();
				if (!parent.exists()) {
					if (!parent.mkdirs()) {
						throw new IOException("Failed to create directory " + parent);
					}
				}
				// Write file data
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}

	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

}
