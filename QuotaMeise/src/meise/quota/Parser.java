package meise.quota;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;

public class Parser {

	private float download;
	private float downloadMax;
	private float downloadPercentage;
	private float upload;
	private float uploadMax;
	private float uploadPercentage;
	
	public Parser(URLConnection connection) {
        BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = in.readLine().toLowerCase();
			
			int downloadFound = 0, uploadFound = 0; //1: now parsing up/download values. 2: now parsing percentage
			int dCount = 0, uCount = 0; // #lines since downloadFound==1 (ore uploadFound==1 respective). | 1: current value. 2: max value.

			while (!line.equals("</html>")) {
				if (line.contains("download")) {
					downloadFound++;
					dCount = 0;
				}
				if (line.contains("upload")) {
					uploadFound++;
					uCount = 0;
				}
				switch(downloadFound) {
				case 1:
					switch (dCount) {
					case 1:
						line = removeBlocks(line);
						line = line.replaceAll("gib", "").replaceAll("mib", "").trim();
						download = Float.parseFloat(line);
						break;
					case 2:
						line = removeBlocks(line);
						line = line.replaceAll("gib", "").replaceAll("mib", "").trim();
						downloadMax = Float.parseFloat(line);
					default:
					}
					dCount++;
					break;
				case 2:
					if (dCount == 1) {
						line = getPercentage(line);
						downloadPercentage = Float.parseFloat(line);
					}
					dCount++;
					break;
				default:
				}
				switch(uploadFound) {
				case 1:
					switch (uCount) {
					case 1:
						line = removeBlocks(line);
						line = line.replaceAll("gib", "").replaceAll("mib", "").trim();
						upload = Float.parseFloat(line);
						break;
					case 2:
						line = removeBlocks(line);
						line = line.replaceAll("gib", "").replaceAll("mib", "").trim();
						uploadMax = Float.parseFloat(line);
					default:
					}
					uCount++;
					break;
				case 2:
					if (uCount == 1) {
						line = getPercentage(line);
						uploadPercentage = Float.parseFloat(line);
					}
					uCount++;
					break;
				default:
				}
				line = in.readLine().toLowerCase();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String removeBlocks(String in) {
		boolean isBlock = false;
		char[] c = in.toCharArray();
		String out = "";
		for (int i = 0; i < c.length; i++) {
			switch (c[i]) {
			case '<':
				isBlock = true;
				break;
			case '>':
				isBlock = false;
				break;
			default:
				if (!isBlock) out += c[i];
			}
		}
		return out;
	}
	
	private String getPercentage(String in) {
		String[] s = in.split("\"");
		return s[1].replaceAll("%", "");
	}
	
	public float getDownload() {
		return download;
	}
	
	public float getDownloadMax() {
		return downloadMax;
	}
	
	public float getDownloadPercentage() {
		return downloadPercentage;
	}
	
	public float getUpload() {
		return upload;
	}
	
	public float getUploadMax() {
		return uploadMax;
	}
	
	public float getUploadPercentage() {
		return uploadPercentage;
	}
	
}
