package meise.quota;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class QuotaStatus {
	
	private Parser p;
	
	public void parse() {
		try {
			URL meise = new URL("https://quota.wohnheim.uni-kl.de/");
			URLConnection connection = meise.openConnection();
			this.p = new Parser();
			this.p.parse(connection);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printToConsole() {
		System.out.println("Qutastatus for UNKNOWN");
		System.out.println("Download: " + p.getDownloadRepresentation() + '/' + p.getDownloadMaxRepresentation());
		System.out.println("Upload: " + p.getUploadRepresentation() + '/' + p.getUploadMaxRepresentation());
	}
	
	public static void main(String[] args) {
		QuotaStatus q = new QuotaStatus();
		q.parse();
		q.printToConsole();
	}
	
}
