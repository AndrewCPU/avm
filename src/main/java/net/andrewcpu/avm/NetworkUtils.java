package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkUtils {
	private static String sendGetRequest(String urlString) throws Exception {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// Set the request method (GET is the default)
		conn.setRequestMethod("GET");

		// Get the response code
		int responseCode = conn.getResponseCode();

		if (responseCode == HttpURLConnection.HTTP_OK) {
			// If the response code is 200 (HTTP_OK), read the response
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();
			return response.toString();
		} else {
			throw new RuntimeException("HTTP GET Request Failed with Error code : " + responseCode);
		}
	}

	public static List<String> getNodeVersionsAvailable() throws Exception {
		String result = sendGetRequest("https://nodejs.org/dist/");
		List<String> versions = new ArrayList<>();
		Pattern pattern = Pattern.compile("v[0-9]+\\.[0-9]+\\.[0-9]+");
		Matcher matcher = pattern.matcher(result);
		while (matcher.find()) {
			versions.add(matcher.group());
		}
		return versions;
	}

	public static File downloadToDirectory(String urlString, File directory) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		directory.mkdirs();
		// Get the file name from the URL
		String fileName = urlString.substring(urlString.lastIndexOf('/') + 1);

		// Open the input and output streams
		try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
		     FileOutputStream out = new FileOutputStream(new File(directory, fileName))) {

			byte[] buffer = new byte[1024];
			int count;
			while ((count = in.read(buffer, 0, 1024)) != -1) {
				out.write(buffer, 0, count);
			}
		} finally {
			connection.disconnect();
		}
		return new File(directory, fileName);
	}


}
