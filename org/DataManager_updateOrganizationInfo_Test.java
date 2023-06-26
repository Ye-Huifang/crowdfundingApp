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
    public void testAddExistingDonationsToUpdatedFund() {
        // Create a mock fund and existing donations
        Fund mockFund = new Fund("12345", "FundName", "FundDescription", 1000);
        List<Donation> existingDonations = new LinkedList<>();
        existingDonations.add(new Donation("Donor1", "name1", 100, "2022-01-01"));
        existingDonations.add(new Donation("Donor2", "name2", 200, "2022-01-02"));
        mockFund.setDonations(existingDonations);

        // Create a mock updated fund
        Fund mockUpdatedFund = new Fund("12345", "NewFundName", "NewFundDescription", 2000);

        // Iterate over the existing donations and add them to the updated fund
        List<Donation> updatedDonations = mockUpdatedFund.getDonations();
        updatedDonations.addAll(existingDonations);

        // Verify that the updated fund has the same donations as the mock fund
        assertEquals(existingDonations.size(), updatedDonations.size());
        for (int i = 0; i < existingDonations.size(); i++) {
            Donation expectedDonation = existingDonations.get(i);
            Donation actualDonation = updatedDonations.get(i);
            assertEquals(expectedDonation.getAmount(), actualDonation.getAmount());
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