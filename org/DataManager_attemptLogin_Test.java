import static org.junit.Assert.*;
import java.util.Map;
import org.junit.Test;
import java.lang.reflect.Field;

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


    @Test(expected=IllegalStateException.class)
    public void testAttemptLoginFailure() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"failure\"}";
            }
        });

        Organization org = dm.attemptLogin("test", "password");
    }

    @Test
    public void testAttemptLoginWithFundAndDonation() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if(resource.equals("/findOrgByLoginAndPassword")) {
                	return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"OrgName\",\"description\":\"OrgDescription\",\"funds\":[{\"_id\":\"fund1\",\"name\":\"fundName1\",\"description\":\"fundDescription1\",\"target\":1000,\"donations\":[{\"contributor\":\"contributor1\",\"amount\":500,\"date\":\"2023-01-01\"}]}, {\"_id\":\"fund2\",\"name\":\"fundName2\",\"description\":\"fundDescription2\",\"target\":400,\"donations\":[{\"contributor\":\"contributor2-1\",\"amount\":100,\"date\":\"2023-01-04\"}, {\"contributor\":\"contributor2-2\",\"amount\":700,\"date\":\"2023-01-02\"}]}]}}";
                } else if(resource.equals("/findContributorNameById")) {
                    return "{\"status\":\"success\",\"data\":\"ContributorName\"}";
                } else {
                    return "{\"status\":\"fail\"}";
                }
            }
        });

        Organization org = dm.attemptLogin("test", "password");
        assertNotNull(org);
        assertEquals("12345", org.getId());
        assertEquals("OrgName", org.getName());
        assertEquals("OrgDescription", org.getDescription());

        // Check fund
        assertEquals(2, org.getFunds().size());
        Fund fund = org.getFunds().get(0);
        assertEquals("fund1", fund.getId());
        assertEquals("fundName1", fund.getName());
        assertEquals("fundDescription1", fund.getDescription());
        assertEquals(1000, fund.getTarget());

        // Check donation
        assertEquals(1, fund.getDonations().size());
        Donation donation = fund.getDonations().get(0);
        assertEquals("fund1", donation.getFundId());
        assertEquals("ContributorName", donation.getContributorName());
        assertEquals(500, donation.getAmount());
        assertEquals("2023-01-01", donation.getDate());
        
        
        // Check fund 2
        Fund fund2 = org.getFunds().get(1);
        assertEquals("fund2", fund2.getId());
        assertEquals("fundName2", fund2.getName());
        assertEquals("fundDescription2", fund2.getDescription());
        assertEquals(400, fund2.getTarget());

        // Check donation 2
        assertEquals(2, fund2.getDonations().size());
        Donation donation_2_1 = fund2.getDonations().get(0);
        assertEquals("fund2", donation_2_1.getFundId());
        assertEquals("ContributorName", donation_2_1.getContributorName());
        assertEquals(100, donation_2_1.getAmount());
        assertEquals("2023-01-04", donation_2_1.getDate());
        
        // Check donation 2-2
        Donation donation_2_2 = fund2.getDonations().get(1);
        assertEquals("fund2", donation_2_2.getFundId());
        assertEquals("ContributorName", donation_2_2.getContributorName());
        assertEquals(700, donation_2_2.getAmount());
        assertEquals("2023-01-02", donation_2_2.getDate());
    }

    @Test(expected=IllegalStateException.class)
    public void testAttemptLoginInvalidJson() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                // Return an invalid JSON string
                return "This is not a valid JSON string";
            }
        });

        // Since the JSON string is invalid, the attemptLogin method should throw an exception
        Organization org = dm.attemptLogin("test", "password");
    }

    @Test
    public void testAttemptLoginNullWebClient() {
        try {
            DataManager dm = new DataManager(null);
            dm.attemptLogin("test", "password");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("WebClient cannot be null", e.getMessage());
        }
    }

    @Test
    public void testAttemptLoginNullLogin() {
        try {
            DataManager dm = new DataManager(new WebClient("localhost", 3001));
            dm.attemptLogin(null, "password");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Login and password cannot be null", e.getMessage());
        }
    }

    @Test
    public void testAttemptLoginNullPassword() {
        try {
            DataManager dm = new DataManager(new WebClient("localhost", 3001));
            dm.attemptLogin("test", null);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Login and password cannot be null", e.getMessage());
        }
    }

    @Test
    public void testAttemptLoginCannotConnectToServer() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        try {
            dm.attemptLogin("test", "password");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Cannot connect to server", e.getMessage());
        }
    }

    @Test
    public void testAttemptLoginJsonNotAnObject() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "[\"This is a JSON array not an object\"]";
            }
        });

        try {
            dm.attemptLogin("test", "password");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Malformed JSON received", e.getMessage());
        }
    }

    @Test
    public void testAttemptLoginStatusError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"Some server error\"}";
            }
        });

        try {
            dm.attemptLogin("test", "password");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Some server error", e.getMessage());
        }
    }

    @Test
    public void testAttemptLoginContributorInCache() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if(resource.equals("/findOrgByLoginAndPassword")) {
                    return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"OrgName\",\"description\":\"OrgDescription\",\"funds\":[{\"_id\":\"fund1\",\"name\":\"fundName1\",\"description\":\"fundDescription1\",\"target\":1000,\"donations\":[{\"contributor\":\"contributor1\",\"amount\":500,\"date\":\"2023-01-01\"}]}]}}";
                } else {
                    return "{\"status\":\"fail\"}";
                }
            }
        });

        // Use reflection to access 'contributorNameCache'
        try {
            Field field = DataManager.class.getDeclaredField("contributorNameCache");
            field.setAccessible(true);
            Map<String, String> contributorNameCache = (Map<String, String>) field.get(dm);
            contributorNameCache.put("contributor1", "CachedName");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access 'contributorNameCache'");
        }

        Organization org = dm.attemptLogin("test", "password");
        assertNotNull(org);
        assertEquals("12345", org.getId());
        assertEquals("OrgName", org.getName());
        assertEquals("OrgDescription", org.getDescription());

        // Check fund
        assertEquals(1, org.getFunds().size());
        Fund fund = org.getFunds().get(0);
        assertEquals("fund1", fund.getId());
        assertEquals("fundName1", fund.getName());
        assertEquals("fundDescription1", fund.getDescription());
        assertEquals(1000, fund.getTarget());

        // Check donation
        assertEquals(1, fund.getDonations().size());
        Donation donation = fund.getDonations().get(0);
        assertEquals("fund1", donation.getFundId());
        assertEquals("CachedName", donation.getContributorName()); // Expect the cached name here
        assertEquals(500, donation.getAmount());
        assertEquals("2023-01-01", donation.getDate());
    }

}