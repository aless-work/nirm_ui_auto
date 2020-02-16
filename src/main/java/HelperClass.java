package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelperClass {

	public String getCode() throws IOException {
		String code = null;
		URL url = null;
		HttpURLConnection connection = null;

		try {
			url = new URL("https://bitbucket.org/mrutyunjaya_neualto/test-automation/src/master/");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String codeFromURL = null;

			while ((codeFromURL = in.readLine()) != null) {
				if (codeFromURL.contains("VerificationCode")) {
					String temp = codeFromURL.replaceAll("\\<.*?\\>", "");
					code = temp.split(":")[1].toString();
				}
			}
			in.close();

			connection.disconnect();
		} catch (Exception e) {

		}

		return code;
	}
}
