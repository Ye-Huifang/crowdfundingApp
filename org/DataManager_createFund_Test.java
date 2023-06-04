import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_createFund_Test {
	
	/*
	 * This is a test class for the DataManager.createFund method.
	 * Add more tests here for this method as needed.
	 * 
	 * When writing tests for other methods, be sure to put them into separate
	 * JUnit test classes.
	 */

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

		Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);
		assertNull(f);
	}
}

public class DataManager_attemptLogin_Test {

	@Test
	public void testAttemptLoginSuccess() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"OrgName\",\"description\":\"OrgDescription\",\"funds\":[]}}";
			}
		});

		Organization org = dm.attemptLogin("test", "password");
		assertNotNull(org);
		assertEquals("12345", org.getId());
		assertEquals("OrgName", org.getName());
		assertEquals("OrgDescription", org.getDescription());
	}


	@Test
	public void testAttemptLoginFailure() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"failure\"}";
			}
		});

		Organization org = dm.attemptLogin("test", "password");
		assertNull(org);
	}
}

public class DataManager_getContributorName_Test {

	@Test
	public void testGetContributorNameSuccess() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":\"Catherine\"}";
			}
		});

		String name = dm.getContributorName("1");
		assertNotNull(name);
		assertEquals("Catherine", name);
	}


	@Test
	public void testGetContributorNameFailure() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"failure\"}";
			}
		});

		String name = dm.getContributorName("1");
		assertNull(name);
	}
}