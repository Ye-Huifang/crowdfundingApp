import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DataManager_createFund_Test {

	@Test
	public void testSuccessfulCreation() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,\"org\":\"5678\",\"donations\":[],\"__v\":0}}";
			}
		});

		Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);

		assertNotNull(f);
		assertEquals("this is the new fund", f.getDescription());
		assertEquals("12345", f.getId());
		assertEquals("new fund", f.getName());
		assertEquals(10000, f.getTarget());
	}

	@Test
	public void testFailedCreation() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"failure\"}";
			}
		});

		try {
			Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);
			assertNull(f);
		} catch (IllegalStateException e) {
			assertEquals("Malformed JSON received", e.getMessage());
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testOtherCreation() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\"}";
			}
		});

		
			Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);
			assertNull(f);
		
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateFundMalformedJson() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				// Return a response with malformed JSON
				return "Invalid JSON response";
			}
		});

		dm.createFund("12345", "new fund", "this is the new fund", 10000);
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateFundNullWebClient() {
		DataManager dm = new DataManager(null);

		dm.createFund("12345", "new fund", "this is the new fund", 10000);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateFundNullOrgId() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));

		dm.createFund(null, "new fund", "this is the new fund", 10000);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateFundEmptyOrgId() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));

		dm.createFund("", "new fund", "this is the new fund", 10000);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateFundNullName() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));

		dm.createFund("12345", null, "this is the new fund", 10000);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateFundEmptyName() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));

		dm.createFund("12345", "", "this is the new fund", 10000);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateFundNullDescription() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));

		dm.createFund("12345", "new fund", null, 10000);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateFundEmptyDescription() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));

		dm.createFund("12345", "new fund", "", 10000);
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateFundNullResponse() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				// Return null for the response
				return null;
			}
		});

		dm.createFund("12345", "new fund", "this is the new fund", 10000);
	}

	@Test
	public void testCreateFundJsonNotAnObject() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "[\"This is a JSON array not an object\"]";
			}
		});

		try {
			dm.createFund("12345", "new fund", "this is the new fund", 10000);
			fail("Expected an IllegalStateException to be thrown");
		} catch (IllegalStateException e) {
			assertEquals("Malformed JSON received", e.getMessage());
		}
	}

}