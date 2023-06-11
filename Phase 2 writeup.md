# 1. Additional Tasks

- **Task 2.4.** Contributor App caching
- **Task 2.7.** Organization App delete fund
- **Task 2.11.** Unattempted Additional Task from Phase 1

# 2. Task Descriptions

## Task 1.1. Organization (Java) App testing

Add `testFailedCreation` and `testCreateFundInvalidJson` unit tests in the existing `DataManager_createFund_Test` to cover the `createFund` method in the `DataManager` class, checking both unsuccessful and invalid fund creation scenarios. Create a new class `DataManager_getContributorName_Test` with `testgetContributorNameSuccess`, `testgetContributorNameFailure` and `testgetContributorNameInvalidJson` unit tests, which test successful, unsuccessful and invalid name retrieval scenarios respectively. Create a new class `DataManager_attemptLogin_Test` with `testAttemptLoginSuccess`, `testAttemptLoginFailure`, `testAttemptLoginWithFundAndDonation` and `testAttemptLoginInvalidJson` unit tests to test the attemptLogin method in the `DataManager` class. I mocked the data returned from the `WebClient.makeRequest` method to simulate different server responses. These three test classes together achieve 100% statement coverage in `DataManager`. 
## Task 1.2. Organization (Java) App debugging

When running the DataManager_attemptLogin_Test.java, the testAttemptLoginSuccess and testAttemptLoginwithFundandDonation didn’t pass. Both of them encountered error on the line `assertEquals("OrgDescription", org.getDescription());` So I checked on the DataManager’s attemptLogin and especially looked for the part where we’re trying to get the description from `/findOrgByLoginAndPassword` request. And on
the line 41 `String description = (String)data.get("descrption");` the key was written as `descrption` which doesn’t align with the string response returned from WebClient. Thus the datamanager was unable to locate the description info. After changing the name, all test cases passed.

## Task 2.3. Organization App aggregate donations by contributor

In the displayFund() method of the userinterface class, I added a treemap with a customized comparable that helps sort the aggregate donations based on the descending order of the donation amount. The treemap uses contributor name as key, and a list as values, the first element of the list is the number of times the contributor has donated, and the second element of the list is the total amount of their donations. After having everything sorted and stored in a treemap, I then iterate through the treemap and retrieve every entry and print them on the console. 

## Task 1.4. Contributor (Android) App testing and debugging

In the test section, I created 4 junit test classes for each method inside `DataManager` class. The attempt login test cases mainly covers 4 scenarios, for example when the login is successful and all fields are correctly read but there is single donation, the case similar to the previous one but have multiple donations, when the login fails, and when the response is not in json format. The `DataManager_getFundName_Test` consists of three test cases, covered accordingly by testSuccess, testFailure, and testException. This test gets the fund name info from `/findFundById` payload. The `DataManager_getAllOrganizations_Test` mainly tests the `getAllOrganization` method which returns a list of Organizations. The response contains a list of Organizations and within each Organization there is a list of funds.

## Task 2.7. Organization App delete fund

This task consists of four parts, adding a deleteFund function in DataManager, displaying and providing navigation in UserInterface, writing Junit test cases and debugging, and implementing defensive programming. In the DataManager I added the deleteFund function which corresponds to the /deleteFund route, then in the UserInterface I added a deletedFund function as well which prompts the user to confirm for the deletion. In the displayFund method in UserInterface I also added prompts for users to choose the option of deleting the current fund. As for testing and defensive programming, I added a DataManager_deletedFund_Test class which resembles the DataManagerRobustnessTest class and passed all the test cases while gaining 100% coverage rate on the deleteFund method.

## Task 1.6. Contributor (Android) App input error handling 

Modified `onMakeDonationButtonClick()` method in MakeDonationActivity class of contributor App to handle invalid donation amounts, including zero and negative values. I first parse the input string to a long datatype, if it’s successful then check whether it is a positive number. If it's not a positive number or a valid value, I use `Toast.makeText()` to display a warning message to the user and clear the input field to prompt them to re-enter a valid value. This prevents any donation with zero or negative amount from being processed.

# 3. Bugs Found and Fixed

## Task 1.2. Organization (Java) App debugging
Fixed the misspelling of “description” to successfully retrieve the description information.
String description = (String)data.get(“descrption”);
`String description = (String)data.get("descrption");`

## Task 1.4. Contributor (Android) App testing and debugging
1. Switched the position of `creditCardExpiryMonth` and `creditCardExpiryYear` to comply with the order in Contributor constructor paraters. 
```
Contributor contributor = new Contributor(id, name, email, creditCardNumber, creditCardCVV, creditCardExpiryYear, creditCardExpiryMonth, creditCardPostCode);
```
2. The input should already be string instead of integer due to the datatype set in contributor class. So i deleted the (Integer) and toString()
```
String creditCardExpiryMonth = ((Integer)data.get("creditCardExpiryMonth")).toString();
String creditCardExpiryYear = ((Integer)
data.get("creditCardExpiryYear")).toString();
```

# 4. Team Contributions

- **Task 2.1.** *Huifang Ye*
- **Task 2.2.** *Ruxue Yan*
- **Task 2.3.** *Clara Zhang*
- **Task 2.4.** *Huifang Ye*
- **Task 2.7.** *Clara Zhang*
- **Task 2.11.** *Ruxue Yan*
