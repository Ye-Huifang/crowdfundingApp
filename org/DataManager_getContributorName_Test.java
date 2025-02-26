import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

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
    public void testGetContributorNameCache() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":\"cz\"}";
            }
        });

        String name = dm.getContributorName("64971ac4722c1b2594748094");
        assertNotNull(name);
        assertEquals("cz", name);
        String name1 = dm.getContributorName("64971ac4722c1b2594748094");
        assertNotNull(name1);
        assertEquals("cz", name1);
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

    @Test(expected = IllegalStateException.class)
    public void testGetContributorNameNullWebClient() {
        DataManager dm = new DataManager(null);
        dm.getContributorName("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetContributorNameNullId() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.getContributorName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetContributorNameEmptyId() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.getContributorName("");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContributorNameNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.getContributorName("1");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContributorNameInvalidJson() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "This is not a valid JSON string";
            }
        });

        dm.getContributorName("1");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContributorNameStatusError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"An error occurred.\"}";
            }
        });
        dm.getContributorName("1");
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateFundErrorStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"An error occurred.\"}";
            }
        });

        dm.createFund("12345", "new fund", "this is the new fund", 10000);
    }

    @Test
    public void testGetContributorNameJsonNotAnObject() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "[\"This is a JSON array not an object\"]";
            }
        });

        try {
            dm.getContributorName("1");
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Malformed JSON received", e.getMessage());
        }
    }

}