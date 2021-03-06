package meise.quota;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;

/**
 * A parser can parse a URLConnection and then holds values for another program to use.
 * @author Tarnadas
 *
 */
public class Parser {

	private float download;
	private String downloadRep;
	private float downloadMax;
	private String downloadMaxRep;
	private float downloadPercentage;
	private float upload;
	private String uploadRep;
	private float uploadMax;
	private String uploadMaxRep;
	private float uploadPercentage;
	
	/**
	 * Parse the answer coming from the URLconnection.
	 * After this, the parser holds all values and simply returns them.
	 * @param connection The connection from which to read.
	 */
	public void parse(URLConnection connection) {
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
						parsedValueLine l = new parsedValueLine(line);
						this.download = l.getValue();
						this.downloadRep = l.getRepresentation();
					case 2:
						parsedValueLine l_ = new parsedValueLine(line);
						this.downloadMax = l_.getValue();
						this.downloadMaxRep = l_.getRepresentation();
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
						parsedValueLine l = new parsedValueLine(line);
						this.upload = l.getValue();
						this.uploadRep = l.getRepresentation();
					case 2:
						parsedValueLine l_ = new parsedValueLine(line);
						this.uploadMax = l_.getValue();
						this.uploadMaxRep = l_.getRepresentation();
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
	
	/**
	 * Can parse a value-holding line into an exact float value and a string representation
	 * @author sylar
	 *
	 */
	static class parsedValueLine {
		
		private float value;
		private String representation;
		
		public float getValue() {
			return this.value;
		}
		public String getRepresentation() {
			return this.representation;
		}
		
		public parsedValueLine(String line) {
			String l = removeBlocks(line);
			int factor = getFactor(l);
			l = l.replaceAll("gib", "").replaceAll("mib", "").trim();
			
			value = Float.parseFloat(l) * factor;
			switch (factor) {
			case 0:
				representation = l;
				break;
			case 1024:
				representation = l + " KiB";
				break;
			case 1024*1024:
				representation = l + " MiB";
				break;
			case 1024*1024*1024:
				representation = l + " GiB";
				break;
			default:
				representation = "ERROR";
			}
		}
	}
	
	/**
	 * Get the factor of KiB, MiB, GiB, ...
	 * @param s must be lower-case and contain the unit
	 * @return the factor.
	 */
	private static int getFactor(String s) {
		int factor = 0;
		if (s.contains("kib")) {
			factor = 1024;
		} else if (s.contains("mib")) {
			factor = 1024*1024;
		} else if (s.contains("gib")) {
			factor = 1024*1024*1024;
		}
		return factor;
	}
	
	/**
	 * Remove XML/HTML blocks
	 * @param in the String to remove the blocks from
	 * @return in without blocks.
	 */
	private static String removeBlocks(String in) {
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
	
	/**
	 * read the percentage from a block.
	 * @param in the line to read from
	 * @return the percentage ready to parse.
	 */
	private static String getPercentage(String in) {
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
	
	public String getUploadRepresentation() {
		return this.uploadRep;
	}
	
	public String getDownloadRepresentation() {
		return this.downloadRep;
	}
	
	public String getUploadMaxRepresentation() {
		return this.uploadMaxRep;
	}
	
	public String getDownloadMaxRepresentation() {
		return this.downloadMaxRep;
	}
	
}
