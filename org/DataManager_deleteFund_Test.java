import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_deleteFund_Test {

	@Test
	public void testSuccessfulDelete() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"target\":100,\"_id\":\"6484c14034fa31339a8bd2d3\",\"name\":\"newfund\",\"description\":\"about to be deleted\",\"org\":\"6483e243b43333338fa5d1e1\",\"donations\":[],\"__v\":0}}";
			}
		});
		
		
		Fund f = dm.deleteFund("6484c14034fa31339a8bd2d3");
		
		assertNotNull(f);
		assertEquals("about to be deleted", f.getDescription());
		assertEquals("6484c14034fa31339a8bd2d3", f.getId());
		assertEquals("newfund", f.getName());
		assertEquals(100, f.getTarget());
	}

	
//	defensive methods
	DataManager dm;

	@Test(expected=IllegalStateException.class)
	public void testDeleteFund_WebClientIsNull() {

		dm = new DataManager(null);
		dm.deleteFund("fundId");
		fail("DataManager.DeleteFund does not throw IllegalStateException when WebClient is null");
		
	}

	@Test(expected=IllegalArgumentException.class)
	public void testDeleteFund_OrgIdIsNull() {

		dm = new DataManager(new WebClient("localhost", 3001));
		dm.deleteFund(null);
		fail("DataManager.DeleteFund does not throw IllegalArgumentxception when orgId is null");
		
	}
	

	@Test(expected=IllegalStateException.class)
	public void testdeleteFund_WebClientCannotConnectToServer() {

		// this assumes no server is running on port 3002
		dm = new DataManager(new WebClient("localhost", 3002));
		dm.deleteFund("fundId");
		fail("DataManager.deleteFund does not throw IllegalStateException when WebClient cannot connect to server");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testdeleteFund_WebClientReturnsNull() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}
		});
		dm.deleteFund("orgId");
		fail("DataManager.deleteFund does not throw IllegalStateException when WebClient returns null");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testdeleteFund_WebClientReturnsError() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}";
			}
		});
		Fund f = dm.deleteFund("orgId");
		fail("DataManager.deleteFund does not throw IllegalStateException when WebClient returns error");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testdeleteFund_WebClientReturnsMalformedJSON() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "I AM NOT JSON!";
			}
		});
		Fund f = dm.deleteFund("orgId");
		fail("DataManager.deleteFund does not throw IllegalStateException when WebClient returns malformed JSON");
		
	}
	

}
