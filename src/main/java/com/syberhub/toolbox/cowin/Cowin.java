package com.syberhub.toolbox.cowin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.syberhub.toolbox.ToolBoxConfig;
import com.syberhub.toolbox.email.EmailService;

/**
 * Cowin slot check
 *
 */
public class Cowin {

	public void checkCowinSlot()
			throws IOException, AddressException, MessagingException, KeyManagementException, NoSuchAlgorithmException {
		String url = buildUrl();

		List<ObjectNode> availableSlots = findAvailableSlots(url);

		if (availableSlots != null && !availableSlots.isEmpty()) {
			String content = createReportText(availableSlots);
			System.out.println(content);
			EmailService.sendGMailNotification("Cowin Notification", content, "text/html");
		} else {
			System.out.println("No available slots. Skipping email.");
			throw new IllegalArgumentException("fff");
		}

	}

	private String createReportText(List<ObjectNode> availableSlots) {
		String content = "<br/>";

		if (availableSlots != null && !availableSlots.isEmpty()) {
			for (ObjectNode s : availableSlots) {
				content += "<br/><br/><br/>========================================<br/>";

				content += "Name: ";
				content += s.get("name");

				content += "<br/>Address: ";
				content += s.get("address");

				content += "<br/>Available: ";
				content += s.get("available_capacity");

				content += "<br/>Age limit: ";
				content += s.get("min_age_limit");

				content += "<br/>Date: ";
				content += s.get("date");

				content += "<br/>Vaccine: ";
				content += s.get("vaccine");
			}
		}
		return content;
	}

	private String buildUrl() {
		String base = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByPin?pincode=";
		String pin = ToolBoxConfig.getProperty("cowin.zipcode");
		String dateparam = "&date=";
		String date = new SimpleDateFormat("dd-MM-YYYY").format(new Date());

		String url = base + pin + dateparam + date;
		System.out.println(url);
		return url;
	}

	private List<ObjectNode> findAvailableSlots(String url)
			throws MalformedURLException, IOException, ProtocolException {
		URL urlForGetRequest = new URL(url);
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept-Language", "hi_IN");
		connection.setRequestProperty("User-Agent", "gecko1_8");
		int responseCode = connection.getResponseCode();

		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			while ((readLine = in.readLine()) != null) {
				response.append(readLine);
			}
			in.close();
			// print result
			System.out.println("JSON String Result " + response.toString());

			ObjectMapper objectMapper = new ObjectMapper();

			// read JSON like DOM Parser
			JsonNode rootNode = objectMapper.readTree(response.toString());
			ArrayNode jsonNode = (ArrayNode) rootNode.get("sessions");

			List<ObjectNode> availableSlots = new ArrayList<ObjectNode>();
			jsonNode.elements().forEachRemaining(n -> validateAndAdd(availableSlots, n));
			return availableSlots;

			// GetAndPost.POSTRequest(response.toString());
		} else {
			System.out.println("GET NOT WORKED: " + connection.getResponseCode());
			String fromFile = new BufferedReader(new InputStreamReader(connection.getErrorStream())).lines()
					.reduce(String::concat).get();
			System.out.println(fromFile);
			return null;
		}
	}

	private void validateAndAdd(List<ObjectNode> availableSlots, JsonNode node) {
		if (node != null && node.isObject()) {
			ObjectNode obj = (ObjectNode) node;

			JsonNode available = obj.get("available_capacity");

			if (available != null && available.isNumber() && available.numberValue().intValue() > 0) {
				availableSlots.add(obj);
			}
		}
	}

}
