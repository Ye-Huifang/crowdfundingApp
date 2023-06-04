import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

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