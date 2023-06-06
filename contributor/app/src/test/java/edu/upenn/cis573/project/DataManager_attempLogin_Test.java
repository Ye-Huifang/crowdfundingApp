package edu.upenn.cis573.project;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_attempLogin_Test {

	@Test
    public void testAttemptLoginSuccess_Single() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	if (resource.equals("/findContributorByLoginAndPassword")) {
            		return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"ConName\",\"email\":\"test@123.com\",\"creditCardNumber\":\"245236234\", \"creditCardCVV\": \"932\", "
                    		+ "\"creditCardExpiryMonth\":\"3\", \"creditCardExpiryYear\":\"2022\",\"creditCardPostCode\":\"25342\",\"donations\":[{\"fund\":\"fund1\",\"date\":\"2023-01-01\",\"amount\" : 214}]}}";
            	} else if (resource.equals("/findFundNameById")) {
            		return "{\"status\":\"success\",\"data\":\"fund1\"}";
            	} else {
            		return null;
            	}
                	
            }
        });

        Contributor con = dm.attemptLogin("test", "password");
        assertNotNull(con);
        assertEquals("12345", con.getId());
        assertEquals("ConName", con.getName());
        assertEquals("test@123.com", con.getEmail());
        assertEquals("245236234", con.getCreditCardNumber());
        assertEquals("932", con.getCreditCardCVV());
        assertEquals("3", con.getCreditCardExpiryMonth());
        assertEquals("2022", con.getCreditCardExpiryYear());
        assertEquals("25342", con.getCreditCardPostCode());
        Donation donation = con.getDonations().get(0);
        
        //check fund
        assertEquals("fund1", donation.getFundName());
        assertEquals("2023-01-01", donation.getDate());
        assertEquals(214, donation.getAmount());
    }
	
	
	@Test
    public void testAttemptLoginSuccess_Multi() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	if (resource.equals("/findContributorByLoginAndPassword")) {
            		return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"ConName\",\"email\":\"test@123.com\",\"creditCardNumber\":\"245236234\", \"creditCardCVV\": \"932\", "
                    		+ "\"creditCardExpiryMonth\":\"3\", \"creditCardExpiryYear\":\"2022\",\"creditCardPostCode\":\"25342\",\"donations\":[{\"fund\":\"fund1\",\"date\":\"2023-01-01\",\"amount\" : 214},"
                    		+ "{\"fund\":\"fund2\",\"date\":\"2021-05-01\",\"amount\" : 323}]}}";
                    	
            	} else if (resource.equals("/findFundNameById")) {
            		return "{\"status\":\"success\",\"data\":\"fund1\"}";
            	} else {
            		return null;
            	}
                	
            }
        });

        Contributor con = dm.attemptLogin("test", "password");
        assertNotNull(con);
        assertEquals("12345", con.getId());
        assertEquals("ConName", con.getName());
        assertEquals("test@123.com", con.getEmail());
        assertEquals("245236234", con.getCreditCardNumber());
        assertEquals("932", con.getCreditCardCVV());
        assertEquals("3", con.getCreditCardExpiryMonth());
        assertEquals("2022", con.getCreditCardExpiryYear());
        
        //check fund 1
        Donation donation1 = con.getDonations().get(0);
        assertEquals("fund1", donation1.getFundName());
        assertEquals("ConName", donation1.getContributorName());
        assertEquals("2023-01-01", donation1.getDate());
        assertEquals(214, donation1.getAmount());
        
        //check fund 2
        Donation donation2 = con.getDonations().get(1);
        assertEquals("fund1", donation2.getFundName());
        assertEquals("ConName", donation2.getContributorName());
        assertEquals("2021-05-01", donation2.getDate());
        assertEquals(323, donation2.getAmount());
    }
	
	@Test
    public void testAttemptLoginFailure() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	return  "{\"status\":\"failure\"}";
                	
            }
        });

        Contributor con = dm.attemptLogin("test", "password");
        assertNull(con);
    }
	
	@Test
    public void testAttemptLoginNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
            	return "Invalid Foramt";
                	
            }
        });

        Contributor con = dm.attemptLogin("test", "password");
        assertNull(con);
    }

}
