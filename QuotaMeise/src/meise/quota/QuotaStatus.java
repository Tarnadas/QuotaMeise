package meise.quota;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class QuotaStatus {
	
	public QuotaStatus() {
		try {
			URL meise = new URL("https://quota.wohnheim.uni-kl.de/");
			URLConnection connection = meise.openConnection();
			new Parser(connection);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new QuotaStatus();
	}
	
}
