import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DataManager_register_Test {
    @Test
    public void testRegisterSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"OrgName\",\"description\":\"OrgDescription\",\"funds\":[]}}";
            }
        });
        Organization org = dm.attemptRegister("test", "password", "OrgName", "OrgDescription");
        assertNotNull(org);
        assertEquals("12345", org.getId());
        assertEquals("OrgName", org.getName());
        assertEquals("OrgDescription", org.getDescription());
    }

    @Test(expected=IllegalStateException.class)
    public void testRegisterFailure() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }
        });

        Organization org = dm.attemptRegister("test", "password", "OrgName", "OrgDescription");
    }

    @Test(expected=IllegalStateException.class)
    public void testRegisterInvalidJson() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                // Return an invalid JSON string
                return "This is not a valid JSON string";
            }
        });

        // Since the JSON string is invalid, the attemptLogin method should throw an exception
        Organization org = dm.attemptRegister("test", "password", "OrgName", "OrgDescription");
    }

    @Test
    public void testAttemptRegisterNullWebClient() {
        try {
            DataManager dm = new DataManager(null);
            dm.attemptRegister("test", "password", "OrgName", "OrgDescription");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("WebClient cannot be null", e.getMessage());
        }
    }

    @Test
    public void testAttemptRegisterNullLogin() {
        try {
            DataManager dm = new DataManager(new WebClient("localhost", 3001));
            dm.attemptRegister(null, "password", "OrgName", "OrgDescription");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Login and password cannot be null", e.getMessage());
        }
    }

    @Test
    public void testAttemptRegisterNullPassword() {
        try {
            DataManager dm = new DataManager(new WebClient("localhost", 3001));
            dm.attemptRegister("login", null, "OrgName", "OrgDescription");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Login and password cannot be null", e.getMessage());
        }
    }

    @Test
    public void testAttemptRegisterNullName() {
        try {
            DataManager dm = new DataManager(new WebClient("localhost", 3001));
            dm.attemptRegister("login", "password", null, "OrgDescription");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Login and password cannot be null", e.getMessage());
        }
    }

    @Test
    public void testAttemptRegisterNullDescription() {
        try {
            DataManager dm = new DataManager(new WebClient("localhost", 3001));
            dm.attemptRegister("login", "password", "OrgName", null);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Login and password cannot be null", e.getMessage());
        }
    }

    @Test
    public void testAttemptRegisterCannotConnectToServer() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        try {
            dm.attemptRegister("test", "password", "OrgName", "OrgDescription");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Cannot connect to server", e.getMessage());
        }
    }

    @Test
    public void testAttemptRegisterJsonNotAnObject() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "[\"This is a JSON array not an object\"]";
            }
        });

        try {
            dm.attemptRegister("login", "password", "OrgName", "OrgDescription");
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
            dm.attemptRegister("login", "password", "OrgName", "OrgDescription");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Some server error", e.getMessage());
        }
    }
}
