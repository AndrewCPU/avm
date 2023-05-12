package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NodeUtil {
	public static String getNodeVersion(File directory) throws Exception {
		// Create a process to execute 'node -v' command
		ProcessBuilder processBuilder = new ProcessBuilder(new File(directory, "node.exe").getAbsolutePath(), "-v");
		Process process = processBuilder.start();

		// Read the output of the command
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String output = reader.readLine();

		// Use regex to parse the output
		Pattern pattern = Pattern.compile("v(\\d+\\.\\d+\\.\\d+)");
		Matcher matcher = pattern.matcher(output);
		if (matcher.find()) {
			return "v" + matcher.group(1);
		} else {
			throw new Exception("Unable to find Node.js version in output: " + output);
		}
	}

	public static File getNodeInstallationDirectory(String version) {
		return new File(AVMUtils.getVersionsDirectory(), "node-" + version + "-win-x64");
	}
	public static List<File> getNodeInstallationDirectories() {
		return Arrays.stream(Objects.requireNonNull(new File(AVMUtils.getAppDataDirectory(), "versions").listFiles())).collect(Collectors.toList());
	}

	public static String getCurrentVersion(){
		try {
			return getNodeVersion(AVMUtils.getMainInstallationPath());
		} catch (Exception e) {
			return "";
		}
	}

	public static List<String> getInstalledVersions() throws Exception {
		List<String> collect = new ArrayList<>();
		for (File file : getNodeInstallationDirectories()) {
			String nodeVersion = getNodeVersion(file);
			collect.add(nodeVersion);
		}
		return collect;
	}

	public static boolean installNodeVersion(String version) throws IOException { // v20.0.0
		try {
			String path = "https://nodejs.org/dist/" + version + "/node-" + version + "-win-x64.zip";
			File zip = NetworkUtils.downloadToDirectory(path, new File(AVMUtils.getAppDataDirectory(), "temp"));
			File versionsDir = new File(AVMUtils.getAppDataDirectory(), "versions");
			versionsDir.mkdirs();
			FileUtils.unzip(zip, versionsDir);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public static boolean uninstallNodeVersion(String version) throws Exception {
		if(getCurrentVersion().equalsIgnoreCase(version)){
			throw new Exception("Switch to a different node version first.");
		}
		File directory = getNodeInstallationDirectory(version);
		try {
			FileUtils.deleteDirectoryRecursively(directory.toPath());
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public static void selectVersion(String version) throws IOException {
		FileUtils.deleteDirectoryRecursively(AVMUtils.getMainInstallationPath().toPath());
		File dir = getNodeInstallationDirectory(version);
		AVMUtils.getMainInstallationPath().mkdirs();
		FileUtils.copyDirectoryRecursively(dir.toPath(), AVMUtils.getMainInstallationPath().toPath());
	}
}
