package edu.upenn.cis573.project;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_makeDonation_Test {

	 @Test
	    public void testmakedonationSuccess() {

	        DataManager dm = new DataManager(new WebClient(null, 0) {

	            @Override
	            public String makeRequest(String resource, Map<String, Object> queryParams) {
	                if (resource.equals("/makeDonation")){
	                	return "{\"status\":\"success\"}";
	                } else {
	                	return null;
	                }
	            	
	            }
	        });

	        boolean deal = dm.makeDonation("12345","54432", "435");
	        assertTrue(deal);


	    }
	 @Test
	   public void testmakedonationFailure() {

	        DataManager dm = new DataManager(new WebClient(null, 0) {

	            @Override
	            public String makeRequest(String resource, Map<String, Object> queryParams) {
	                return "Invalid Format";
	            	
	            }
	        });

	        boolean deal = dm.makeDonation("12345","54432", "435");
	        assertFalse(deal);


	    }

}
