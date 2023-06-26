import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_makeDonation_Test {

	@Test
    public void testMakeDonationSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	if (resource.equals("/makeDonation")) {
            		return "{\"status\":\"success\",\"data\":{\"_id\":\"64985b2f119c4325c0fbc58e\",\"contributor\":\"64971ac4722c1b2594748094\",\"fund\":\"64971ab0722c1b259474808b\",\"date\":\"2023-06-25T15:20:15.128Z\",\"amount\":14,\"__v\":0}}";
                    
            	} else if (resource.equals("/findContributorNameById")){
            		return "{\"status\":\"success\",\"data\":\"contributor1\"}";
              
            	}
            	return "";
                 
            }
        });

        Donation d = dm.makeDonation("1","1","11");
        assertEquals("64971ab0722c1b259474808b", d.getFundId());
        assertEquals("contributor1", d.getContributorName());
        assertEquals(14, d.getAmount());
        assertEquals("2023-06-25T15:20:15.128Z", d.getDate());
    }


	 @Test(expected=IllegalStateException.class)
    public void testMakeDonationFailure() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	if (resource.equals("/makeDonation")) {
            		return "{\"status\":\"error\"}";
                    
            	} else if (resource.equals("/findContributorNameById")){
            		return "{\"status\":\"success\",\"data\":\"contributor1\"}";
              
            	}
            	return "";
            }
        });

        Donation d = dm.makeDonation("1","1","11");
	 }
	 
    @Test(expected=IllegalStateException.class)
    public void testmakeDonationInvalidJson() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "This is not a valid JSON string";
            }
        });

               Donation d = dm.makeDonation("1","1","11");

    }
    
    @Test(expected=IllegalStateException.class)
    public void testmakeDonationNullClient() {
    	DataManager dm = new DataManager(null);
        Donation d = dm.makeDonation("1","1","11");

    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testmakeDonationNullcontributorid() {
    	DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	if (resource.equals("/makeDonation")) {
            		return "{\"status\":\"success\",\"data\":{\"_id\":\"64985b2f119c4325c0fbc58e\",\"contributor\":\"64971ac4722c1b2594748094\",\"fund\":\"64971ab0722c1b259474808b\",\"date\":\"2023-06-25T15:20:15.128Z\",\"amount\":14,\"__v\":0}}";
                    
            	} else if (resource.equals("/findContributorNameById")){
            		return "{\"status\":\"success\",\"data\":\"contributor1\"}";
              
            	}
            	return "";
                 
            }
        });
        Donation d = dm.makeDonation(null,"1","11");

    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testmakeDonationEmptycontributorid() {
    	DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	if (resource.equals("/makeDonation")) {
            		return "{\"status\":\"success\",\"data\":{\"_id\":\"64985b2f119c4325c0fbc58e\",\"contributor\":\"64971ac4722c1b2594748094\",\"fund\":\"64971ab0722c1b259474808b\",\"date\":\"2023-06-25T15:20:15.128Z\",\"amount\":14,\"__v\":0}}";
                    
            	} else if (resource.equals("/findContributorNameById")){
            		return "{\"status\":\"success\",\"data\":\"contributor1\"}";
              
            	}
            	return "";
                 
            }
        });
        Donation d = dm.makeDonation("","1","11");

    }
    @Test(expected=IllegalArgumentException.class)
    public void testmakeDonationNullfundid() {
    	DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	if (resource.equals("/makeDonation")) {
            		return "{\"status\":\"success\",\"data\":{\"_id\":\"64985b2f119c4325c0fbc58e\",\"contributor\":\"64971ac4722c1b2594748094\",\"fund\":\"64971ab0722c1b259474808b\",\"date\":\"2023-06-25T15:20:15.128Z\",\"amount\":14,\"__v\":0}}";
                    
            	} else if (resource.equals("/findContributorNameById")){
            		return "{\"status\":\"success\",\"data\":\"contributor1\"}";
              
            	}
            	return "";
                 
            }
        });
        Donation d = dm.makeDonation("1",null,"11");

    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testmakeDonationEmptyfundid() {
    	DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	if (resource.equals("/makeDonation")) {
            		return "{\"status\":\"success\",\"data\":{\"_id\":\"64985b2f119c4325c0fbc58e\",\"contributor\":\"64971ac4722c1b2594748094\",\"fund\":\"64971ab0722c1b259474808b\",\"date\":\"2023-06-25T15:20:15.128Z\",\"amount\":14,\"__v\":0}}";
                    
            	} else if (resource.equals("/findContributorNameById")){
            		return "{\"status\":\"success\",\"data\":\"contributor1\"}";
              
            	}
            	return "";
                 
            }
        });
        Donation d = dm.makeDonation("1","","11");

    }
    @Test(expected=IllegalArgumentException.class)
    public void testmakeDonationNullAmount() {
    	DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	if (resource.equals("/makeDonation")) {
            		return "{\"status\":\"success\",\"data\":{\"_id\":\"64985b2f119c4325c0fbc58e\",\"contributor\":\"64971ac4722c1b2594748094\",\"fund\":\"64971ab0722c1b259474808b\",\"date\":\"2023-06-25T15:20:15.128Z\",\"amount\":14,\"__v\":0}}";
                    
            	} else if (resource.equals("/findContributorNameById")){
            		return "{\"status\":\"success\",\"data\":\"contributor1\"}";
              
            	}
            	return "";
                 
            }
        });
        Donation d = dm.makeDonation("1", "1",null);

    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testmakeDonationEmptyAmount() {
    	DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	if (resource.equals("/makeDonation")) {
            		return "{\"status\":\"success\",\"data\":{\"_id\":\"64985b2f119c4325c0fbc58e\",\"contributor\":\"64971ac4722c1b2594748094\",\"fund\":\"64971ab0722c1b259474808b\",\"date\":\"2023-06-25T15:20:15.128Z\",\"amount\":14,\"__v\":0}}";
                    
            	} else if (resource.equals("/findContributorNameById")){
            		return "{\"status\":\"success\",\"data\":\"contributor1\"}";
              
            	}
            	return "";
                 
            }
        });
        Donation d = dm.makeDonation("1","1","");
    }
    
    @Test
    public void testmakeDonationOther() {
    	DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	return "{\"status\":\"failure\"}";
                 
            }
        });
        assertNull(dm.makeDonation("1","1","11"));
    }
    
    @Test(expected=IllegalStateException.class)
    public void testmakeDonationNUllResponse() {
    	DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	return null;
                 
            }
        });
        assertNull(dm.makeDonation("6498f7492e591226e8536ab8","64987b0b2e591226e8536aaf","11"));
    }


}
