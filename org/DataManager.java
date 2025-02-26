import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DataManager {

	private final WebClient client;
	private Map<String, String> contributorNameCache;

	public DataManager(WebClient client) {
		this.client = client;
		this.contributorNameCache = new HashMap<>();
	}

	public Organization attemptLogin(String login, String password) {
		if (client == null) {
			throw new IllegalStateException("WebClient cannot be null");
		}
		if (login == null || password == null) {
			throw new IllegalArgumentException("Login and password cannot be null");
		}

		try {
			Map<String, Object> map = new HashMap<>();
			map.put("login", login);
			map.put("password", password);
			String response = client.makeRequest("/findOrgByLoginAndPassword", map);
			if (response == null) {
				throw new IllegalStateException("Cannot connect to server");
			}

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(response);
			if (!(obj instanceof JSONObject)) {
				throw new IllegalStateException("Malformed JSON received");
			}
			JSONObject json = (JSONObject) obj;

			String status = (String) json.get("status");

			if (status.equals("success")) {
				JSONObject data = (JSONObject) json.get("data");
				String fundId = (String) data.get("_id");
				String name = (String) data.get("name");
				String description = (String) data.get("description");
				Organization org = new Organization(fundId, name, description);

				JSONArray funds = (JSONArray) data.get("funds");
				Iterator<?> it = funds.iterator();
				while (it.hasNext()) {
					JSONObject fund = (JSONObject) it.next();
					fundId = (String) fund.get("_id");
					name = (String) fund.get("name");
					description = (String) fund.get("description");
					long target = (Long) fund.get("target");

					Fund newFund = new Fund(fundId, name, description, target);

					JSONArray donations = (JSONArray) fund.get("donations");
					List<Donation> donationList = new LinkedList<>();
					Iterator<?> it2 = donations.iterator();
					while (it2.hasNext()) {
						JSONObject donation = (JSONObject) it2.next();
						String contributorId = (String) donation.get("contributor");

						String contributorName = null;
						contributorName = this.getContributorName(contributorId);
						long amount = (Long) donation.get("amount");
						String date = (String) donation.get("date");
						donationList.add(new Donation(fundId, contributorName, amount, date));
					}

					newFund.setDonations(donationList);

					org.addFund(newFund);
				}
				return org;
			} else if (status.equals("error")) {
				String errorMessage = (String) json.get("error");
				throw new IllegalStateException(errorMessage);
			} else {
				throw new IllegalStateException("Malformed JSON received");
			}
		} catch (ParseException e) {
			throw new IllegalStateException("Malformed JSON received");
		}
	}

	public Organization attemptRegister(String login, String password, String name, String description) {
		if (client == null) {
			throw new IllegalStateException("WebClient cannot be null");
		}
		if (login == null || password == null || name == null || description == null) {
			throw new IllegalArgumentException("Login and password cannot be null");
		}
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("login", login);
			map.put("password", password);
			map.put("name", name);
			map.put("description", description);
			String response = client.makeRequest("/createOrganization", map);
			System.out.println(response);
			if (response == null) {
				throw new IllegalStateException("Cannot connect to server");
			}

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(response);
			if (!(obj instanceof JSONObject)) {
				throw new IllegalStateException("Malformed JSON received");
			}
			JSONObject json = (JSONObject) obj;

			String status = (String) json.get("status");

			if (status.equals("success")) {
				Organization org = attemptLogin(login, password);
				return org;
			} else if (status.equals("error")) {
				String errorMessage = (String) json.get("error");
				throw new IllegalStateException(errorMessage);
			} else {
				throw new IllegalStateException("Malformed JSON received");
			}
		} catch (ParseException e) {
			throw new IllegalStateException("Malformed JSON received");
		}
	}

	public void attemptChangePassword(Organization org, String newPassword) {
		if (client == null) {
			throw new IllegalStateException("WebClient cannot be null");
		}
		if (org == null || newPassword == null) {
			throw new IllegalArgumentException("org and password cannot be null");
		}
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("id", org.getId());
			map.put("password", newPassword);

			String response = client.makeRequest("/updateOrganization", map);
			if (response == null) {
				throw new IllegalStateException("Cannot connect to server");
			}

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(response);
			if (!(obj instanceof JSONObject)) {
				throw new IllegalStateException("Malformed JSON received");
			}
			JSONObject json = (JSONObject) obj;

			String status = (String) json.get("status");

			if (status.equals("success")) {
				return;
			} else if (status.equals("error")) {
				String errorMessage = (String) json.get("error");
				throw new IllegalStateException(errorMessage);
			} else {
				throw new IllegalStateException("Malformed JSON received");
			}
		} catch (ParseException e) {
			throw new IllegalStateException("Malformed JSON received");
		}
	}

	public String getContributorName(String id) {
		if (client == null) {
			throw new IllegalStateException("WebClient cannot be null");
		}
		if (id == null || id.isEmpty()) {
			throw new IllegalArgumentException("id cannot be null or empty");
		}
		if (contributorNameCache.containsKey(id)) {
			return contributorNameCache.get(id);
		}
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("id", id);
			String response = client.makeRequest("/findContributorNameById", map);

			if (response == null) {
				throw new IllegalStateException("Cannot connect to server");
			}

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(response);
			if (!(obj instanceof JSONObject)) {
				throw new IllegalStateException("Malformed JSON received");
			}
			JSONObject json = (JSONObject) obj;

			String status = (String) json.get("status");
			if (status.equals("error")) {
				String errorMessage = (String) json.get("error");
				System.out.println("warning: an error occured");
				throw new IllegalStateException(errorMessage);
			}

			if (status.equals("success")) {
				String name = (String) json.get("data");
				contributorNameCache.put(id, name);
				return name;
			} else {
				return null;
			}
		} catch (ParseException e) {
			throw new IllegalStateException("Malformed JSON received");
		}
	}


	public Fund createFund(String orgId, String name, String description, long target) {
		if (client == null) {
			throw new IllegalStateException("WebClient cannot be null");
		}
		if (orgId == null || orgId.isEmpty() || name == null || name.isEmpty() || description == null
				|| description.isEmpty()) {
			throw new IllegalArgumentException("orgId, name, or description cannot be null or empty");
		}

		try {
			Map<String, Object> map = new HashMap<>();
			map.put("orgId", orgId);
			map.put("name", name);
			map.put("description", description);
			map.put("target", target);
			String response = client.makeRequest("/createFund", map);

			if (response == null) {
				throw new IllegalStateException("WebClient returned null response");
			}

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(response);
			if (!(obj instanceof JSONObject)) {
				throw new IllegalStateException("Malformed JSON received");
			}
			JSONObject json = (JSONObject) obj;

			String status = (String) json.get("status");
			if (status.equals("error")) {
				String errorMessage = (String) json.get("error");
				throw new IllegalStateException(errorMessage);
			}

			if (status.equals("success")) {
				JSONObject fund = (JSONObject) json.get("data");
				String fundId = (String) fund.get("_id");
				return new Fund(fundId, name, description, target);
			} else {
				return null;
			}
		} catch (ParseException e) {
			throw new IllegalStateException("Malformed JSON received");
		}
	}


	public Fund deleteFund(String fundId) {
		if (client == null) {
			throw new IllegalStateException();
		}
		if (fundId == null) {
			throw new IllegalArgumentException();
		}
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("id", fundId);
			String response = client.makeRequest("/deleteFund", map);
			if (response == null) {
				throw new IllegalStateException();
			}
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String) json.get("status");

			if (status.equals("success")) {
				JSONObject fund = (JSONObject) json.get("data");
				String name = (String) fund.get("name");
				String description = (String) fund.get("description");
				long target = (Long) fund.get("target");
				return new Fund(fundId, name, description, target);
			} else {
				throw new IllegalStateException();
			}
		} catch (ParseException e) {
			throw new IllegalStateException("Malformed JSON received");
		}
	}


	public Organization updateOrganizationInfo(String orgId, String orgName, String orgDescription) {
		if (client == null) {
			throw new IllegalStateException("WebClient cannot be null");
		}
		if (orgId == null || orgId.isEmpty()) {
			throw new IllegalArgumentException("orgId cannot be null or empty");
		}

		try {
			Map<String, Object> map = new HashMap<>();
			map.put("id", orgId);
			map.put("name", orgName);
			map.put("description", orgDescription);
			String response = client.makeRequest("/editAccount", map);

			if (response == null) {
				throw new IllegalStateException("Cannot connect to server");
			}

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(response);
			if (!(obj instanceof JSONObject)) {
				throw new IllegalStateException("Malformed JSON received");
			}
			JSONObject json = (JSONObject) obj;

			String status = (String) json.get("status");
			if (status.equals("error")) {
				String errorMessage = (String) json.get("error");
				throw new IllegalStateException(errorMessage);
			}

			if (status.equals("success")) {
				JSONObject data = (JSONObject) json.get("data");
				String name = (String) data.get("name");
				String description = (String) data.get("description");

				Organization updatedOrg = new Organization(orgId, name, description);

				// Update funds for the organization
				JSONArray fundsArray = (JSONArray) data.get("funds");
				if (fundsArray != null) {
					List<Fund> funds = new ArrayList<>();
					for (Object fundObj : fundsArray) {
						if (fundObj instanceof JSONObject) {
							JSONObject fundJson = (JSONObject) fundObj;
							String fundId = (String) fundJson.get("id");
							String fundName = (String) fundJson.get("name");
							String fundDescription = (String) fundJson.get("description");
							long fundTarget = (long) fundJson.get("target");
							Fund fund = new Fund(fundId, fundName, fundDescription, fundTarget);
							funds.add(fund);
						}
					}
					updatedOrg.setFund(funds);
				}

				return updatedOrg;
			} else {
				return null;
			}
		} catch (ParseException e) {
			throw new IllegalStateException("Malformed JSON received");
		}
	}


	public Donation makeDonation(String contributorId, String fundId, String amount) {
		if (client == null) {
			throw new IllegalStateException("WebClient cannot be null");
		}
		if (fundId == null || fundId.isEmpty() || contributorId == null || contributorId.isEmpty() || amount == null || amount.isEmpty() ) {
			throw new IllegalArgumentException("cannot be null or empty");
		}
		
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("contributor", contributorId);
			map.put("fund", fundId); 
			map.put("amount", amount);
			String response = client.makeRequest("/makeDonation", map);
			if (response == null) {
				throw new IllegalStateException("Cannot connect to server");
			}
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(response);
			JSONObject json = (JSONObject) obj;
			String status = (String) json.get("status");
			if (status.equals("error")) {
				String errorMessage = (String) json.get("error");
				throw new IllegalStateException(errorMessage);
			}
			if (status.equals("success")) {
				JSONObject data = (JSONObject) json.get("data");
				String d_fundid = (String) data.get("fund");
				String d_contributorid = (String) data.get("contributor");
				String d_date = (String) data.get("date");
				long d_amount = (long) data.get("amount");
				String d_contributor_name = getContributorName(d_contributorid);
				Donation d = new Donation(d_fundid, d_contributor_name, d_amount, d_date);
				return d;
			} else {
				return null;
			}
		} catch (ParseException e) {
			throw new IllegalStateException("Malformed JSON received");
		}
	}



}