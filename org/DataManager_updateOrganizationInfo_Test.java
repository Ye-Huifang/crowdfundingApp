import static org.junit.Assert.*;

import java.util.Map;

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