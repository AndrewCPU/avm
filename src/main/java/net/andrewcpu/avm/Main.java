package org.example;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
	public static void main(String[] args) throws Exception {
		// Define command line options
		Options options = new Options();
		options.addOption("ls", false, "List all installed versions");
		options.addOption("install", true, "Install a specific version");
		options.addOption("uninstall", true, "Uninstall a specific version");
		options.addOption("use", true, "Use a specific version");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption("ls")) {
			List<String> installedVersions = NodeUtil.getInstalledVersions();
			String currentVersion = NodeUtil.getCurrentVersion();
			for (String version : installedVersions) {
				String prefix = version.equals(currentVersion) ? "* " : "- ";
				System.out.println(prefix + version);
			}
		} else if (cmd.hasOption("install")) {
			String version = cmd.getOptionValue("install");
			if (version == null) {
				List<String> availableVersions = NetworkUtils.getNodeVersionsAvailable();
				for (String availableVersion : availableVersions) {
					System.out.println(availableVersion);
				}
			} else {
				boolean success = NodeUtil.installNodeVersion(version);
				System.out.println(success ? "Successfully installed " + version : "Failed to install " + version);
			}
		} else if (cmd.hasOption("uninstall")) {
			String version = cmd.getOptionValue("uninstall");
			boolean success = NodeUtil.uninstallNodeVersion(version);
			System.out.println(success ? "Successfully uninstalled " + version : "Failed to uninstall " + version);
		} else if (cmd.hasOption("use")) {
			String version = cmd.getOptionValue("use");
			try {
				NodeUtil.selectVersion(version);
				System.out.println("Successfully switched to " + version);
			} catch (IOException e) {
				System.out.println("Failed to switch to '" + version + "'. Error: " + e.getMessage());
			}
		} else {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("avm", options);
		}
	}
}
