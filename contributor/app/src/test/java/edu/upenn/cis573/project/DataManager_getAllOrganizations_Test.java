package edu.upenn.cis573.project;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class DataManager_getAllOrganizations_Test {

	@Test
    public void testgetAllOrganizationsSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	if (resource.equals("/allOrgs")) {
            		return "{\"status\":\"success\",\"data\":[{\"_id\": \"343\",\"name\": \"dataname\",\"funds\": [{\"_id\":\"321\", \"name\": \"fundname\",\"target\": 243, \"totalDonations\":243}]}]}}";
                    	
            	} else {
            		return null;
            	}
                	
            }
        });

        List<Organization> list = dm.getAllOrganizations();
        System.out.println(list);
        assertNotNull(list);
        Organization org = list.get(0);
        assertEquals("343", org.getId());
        assertEquals("dataname", org.getName());
        List<Fund> funds = org.getFunds();
        assertNotNull(funds);
        Fund f = funds.get(0);
        assertEquals("321", f.getId());
        assertEquals("fundname", f.getName());
        assertEquals(243, f.getTarget());
        assertEquals(243, f.getTotalDonations());

    }
	
	@Test
    public void testgetAllOrganizationsFailure() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	 return "{\"status\":\"failure\"}";
                	
            }
        });

        List<Organization> list = dm.getAllOrganizations();
        assertNull(list);
    }
	
	@Test
    public void testgetAllOrganizationsNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	 return "Invalid Format";
                	
            }
        });

        List<Organization> list = dm.getAllOrganizations();
        assertNull(list);
    }

}
