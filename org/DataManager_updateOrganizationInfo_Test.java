import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

public class DataManager_updateOrganizationInfo_Test {

    @Test
    public void testUpdateOrganizationInfoSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"name\":\"NewOrgName\",\"description\":\"NewOrgDescription\"}}";
            }
        });

        Organization org = dm.updateOrganizationInfo("12345", "NewOrgName", "NewOrgDescription");
        assertNotNull(org);
        assertEquals("12345", org.getId());
        assertEquals("NewOrgName", org.getName());
        assertEquals("NewOrgDescription", org.getDescription());
    }

    @Test
    public void testUpdateOrganizationInfoFailure() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"An error occurred.\"}";
            }
        });

        try {
            dm.updateOrganizationInfo("12345", "NewOrgName", "NewOrgDescription");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("An error occurred.", e.getMessage());
        }
    }

    @Test
    public void testUpdateOrganizationInfoWithExistingFunds() {
        // Create some funds and add them to an organization
        List<Fund> funds = new ArrayList<>();
        Fund fund1 = new Fund("f1", "Fund1", "Fund 1 description", 1000);
        Fund fund2 = new Fund("f2", "Fund2", "Fund 2 description", 2000);
        funds.add(fund1);
        funds.add(fund2);

        Organization org = new Organization("12345", "OldOrgName", "OldOrgDescription");
        org.setFund(funds);

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"name\":\"NewOrgName\",\"description\":\"NewOrgDescription\",\"funds\":[{\"id\":\"f1\",\"name\":\"Fund1\",\"description\":\"Fund 1 description\",\"target\":1000},{\"id\":\"f2\",\"name\":\"Fund2\",\"description\":\"Fund 2 description\",\"target\":2000}]}}";
            }
        });

        // Pass the correct orgId "12345" here
        Organization updatedOrg = dm.updateOrganizationInfo("12345", "NewOrgName", "NewOrgDescription");
        assertNotNull(updatedOrg);
        assertEquals("12345", updatedOrg.getId());
        assertEquals("NewOrgName", updatedOrg.getName());
        assertEquals("NewOrgDescription", updatedOrg.getDescription());

        // Assert that the funds were added to the organization
        List<Fund> existingFunds = updatedOrg.getFunds();
        assertEquals(2, existingFunds.size());

        Fund existingFund1 = existingFunds.get(0);
        assertEquals("f1", existingFund1.getId());
        assertEquals("Fund1", existingFund1.getName());
        assertEquals("Fund 1 description", existingFund1.getDescription());

        Fund existingFund2 = existingFunds.get(1);
        assertEquals("f2", existingFund2.getId());
        assertEquals("Fund2", existingFund2.getName());
        assertEquals("Fund 2 description", existingFund2.getDescription());
    }


    @Test
    public void testUpdateOrganizationInfoMalformedJson() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "This is not a JSON object";
            }
        });

        try {
            dm.updateOrganizationInfo("12345", "NewOrgName", "NewOrgDescription");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Malformed JSON received", e.getMessage());
        }
    }

    @Test
    public void testUpdateOrganizationInfoNullWebClient() {
        DataManager dm = new DataManager(null);

        try {
            dm.updateOrganizationInfo("12345", "NewOrgName", "NewOrgDescription");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("WebClient cannot be null", e.getMessage());
        }
    }

    @Test
    public void testUpdateOrganizationInfoNullOrgId() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"name\":\"NewOrgName\",\"description\":\"NewOrgDescription\"}}";
            }
        });

        try {
            dm.updateOrganizationInfo(null, "NewOrgName", "NewOrgDescription");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("orgId cannot be null or empty", e.getMessage());
        }
    }

    @Test
    public void testUpdateOrganizationInfoEmptyOrgId() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"name\":\"NewOrgName\",\"description\":\"NewOrgDescription\"}}";
            }
        });

        try {
            dm.updateOrganizationInfo("", "NewOrgName", "NewOrgDescription");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("orgId cannot be null or empty", e.getMessage());
        }
    }

    @Test
    public void testUpdateOrganizationInfoCannotConnectToServer() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        try {
            dm.updateOrganizationInfo("12345", "NewOrgName", "NewOrgDescription");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Cannot connect to server", e.getMessage());
        }
    }

    @Test
    public void testUpdateOrganizationInfoInvalidStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"invalid\",\"data\":{}}";
            }
        });

        Organization org = dm.updateOrganizationInfo("12345", "NewOrgName", "NewOrgDescription");
        assertNull(org);
    }

    @Test
    public void testUpdateOrganizationInfoJsonNotAnObject() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "[\"This is a JSON array not an object\"]";
            }
        });

        try {
            dm.updateOrganizationInfo("12345", "NewOrgName", "NewOrgDescription");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Malformed JSON received", e.getMessage());
        }
    }

}