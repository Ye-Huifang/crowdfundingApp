import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

public class DataManager_attemptChangePassword_Test {

    @Test
    public void testChangePasswordSuccess() {
        Organization org = new Organization("12345", "OldOrgName", "OldOrgDescription");
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\"}";
            }
        });
        dm.attemptChangePassword(org, "NewPassword");
    }

    @Test(expected = IllegalStateException.class)
    public void testChangePasswordFailure() {
        Organization org = new Organization("12345", "OldOrgName", "OldOrgDescription");
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"Password change failed\"}";
            }
        });
        dm.attemptChangePassword(org, "NewPassword");
    }

    @Test(expected = IllegalStateException.class)
    public void testChangePasswordInvalidJson() {
        Organization org = new Organization("12345", "OldOrgName", "OldOrgDescription");
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "This is not a valid JSON string";
            }
        });
        dm.attemptChangePassword(org, "NewPassword");
    }

    @Test
    public void testChangePasswordNullWebClient() {
        try {
            DataManager dm = new DataManager(null);
            dm.attemptChangePassword(new Organization("12345", "OldOrgName", "OldOrgDescription"), "NewPassword");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("WebClient cannot be null", e.getMessage());
        }
    }

    @Test
    public void testChangePasswordNullOrg() {
        try {
            DataManager dm = new DataManager(new WebClient("localhost", 3001));
            dm.attemptChangePassword(null, "NewPassword");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("org and password cannot be null", e.getMessage());
        }
    }

    @Test
    public void testChangePasswordNullPassword() {
        try {
            DataManager dm = new DataManager(new WebClient("localhost", 3001));
            dm.attemptChangePassword(new Organization("12345", "OldOrgName", "OldOrgDescription"), null);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("org and password cannot be null", e.getMessage());
        }
    }

    @Test
    public void testChangePasswordCannotConnectToServer() {
        Organization org = new Organization("12345", "OldOrgName", "OldOrgDescription");
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        try {
            dm.attemptChangePassword(org, "NewPassword");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Cannot connect to server", e.getMessage());
        }
    }

    @Test
    public void testChangePasswordJsonNotAnObject() {
        Organization org = new Organization("12345", "OldOrgName", "OldOrgDescription");
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "[\"This is a JSON array not an object\"]";
            }
        });

        try {
            dm.attemptChangePassword(org, "NewPassword");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Malformed JSON received", e.getMessage());
        }
    }

    @Test
    public void testChangePasswordStatusError() {
        Organization org = new Organization("12345", "OldOrgName", "OldOrgDescription");
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"Some server error\"}";
            }
        });

        try {
            dm.attemptChangePassword(org, "NewPassword");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Some server error", e.getMessage());
        }
    }

    @Test
    public void testChangePasswordMalformedJson() {
        Organization org = new Organization("12345", "OldOrgName", "OldOrgDescription");
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"invalid_status\"}";
            }
        });

        try {
            dm.attemptChangePassword(org, "NewPassword");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Malformed JSON received", e.getMessage());
        }
    }
}