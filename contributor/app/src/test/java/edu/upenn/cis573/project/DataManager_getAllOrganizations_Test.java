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
            		return "{\"status\":\"success\",\"data\":[{\"_id\": \"343\",\"name\": \"dataname\",\"funds\": [{\"_id\":\"321\", \"name\": \"fundname\",\"target\": 243, \"totalDonations\":243}"
            				+ ",{\"_id\":\"311\", \"name\": \"fundname2\",\"target\": 462, \"totalDonations\":674}]}]}}";
                    	
            	} else {
            		return null;
            	}
                	
            }
        });

        List<Organization> list = dm.getAllOrganizations();
        assertEquals(1, list.size());
        assertNotNull(list);
        Organization org = list.get(0);
        assertEquals("343", org.getId());
        assertEquals("dataname", org.getName());
        List<Fund> funds = org.getFunds();
        assertNotNull(funds);
        
        //check fund 1
        Fund f1 = funds.get(0);
        assertEquals("321", f1.getId());
        assertEquals("fundname", f1.getName());
        assertEquals(243, f1.getTarget());
        assertEquals(243, f1.getTotalDonations());
        
        //check fund 2
        Fund f2 = funds.get(1);
        assertEquals("311", f2.getId());
        assertEquals("fundname2", f2.getName());
        assertEquals(462, f2.getTarget());
        assertEquals(674, f2.getTotalDonations());

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
