import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

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

    @Test
    public void testGetContributorNameInvalidJson() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                // Return an invalid JSON string
                return "This is not a valid JSON string";
            }
        });

        // Since the JSON string is invalid, the getContributorName method should throw an exception and return null
        String name = dm.getContributorName("testId");
        assertNull(name);
    }

}