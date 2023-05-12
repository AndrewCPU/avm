package org.example;

import java.io.File;

public class AVMUtils {
	public static File getAppDataDirectory() {
		String appData = System.getenv("APPDATA");
		if (appData == null) {
			throw new IllegalStateException("APPDATA environment variable is not set");
		}
		return new File(appData, ".avm");
	}

	public static File getConfigFile() {
		return new File(getAppDataDirectory(), ".cfg");
	}

	public static File getVersionsDirectory() {
		return new File(getAppDataDirectory(), "versions");
	}

	public static File getMainInstallationPath() {
		return new File(getAppDataDirectory(), "node");
	}
}
