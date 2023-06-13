# 1. Additional Tasks

- **Task 2.4.** Contributor App caching
- **Task 2.7.** Organization App delete fund
- **Task 2.11.** Unattempted Additional Task from Phase 1

# 2. Task Descriptions

## Task 2.1. Organization App caching
We first added a new Map instance variable `contributorNameCache` in the `DataManager` class. This Map was implemented as a HashMap to store the name of a contributor against their ID once fetched from the RESTful API. The cache was keyed by the contributor's ID and the value was the contributor's name. Then we also initialized `contributorNameCache` in the `DataManager` constructor. Finally, we changed the way the `attemptLogin` method retrieved the contributor's name. We first checked if `contributorNameCache` already contained the name for the current contributor's ID. If it did, we retrieved the contributor's name from the cache. If not, we called the `getContributorName` method to fetch the name from the API, and then stored this value in `contributorNameCache` for future reference.

## Task 1.2. Organization (Java) App debugging

When running the DataManager_attemptLogin_Test.java, the testAttemptLoginSuccess and testAttemptLoginwithFundandDonation didn’t pass. Both of them encountered error on the line `assertEquals("OrgDescription", org.getDescription());` So I checked on the DataManager’s attemptLogin and especially looked for the part where we’re trying to get the description from `/findOrgByLoginAndPassword` request. And on
the line 41 `String description = (String)data.get("descrption");` the key was written as `descrption` which doesn’t align with the string response returned from WebClient. Thus the datamanager was unable to locate the description info. After changing the name, all test cases passed.

## Task 2.3. Organization App aggregate donations by contributor

In the `displayFund()` method of the userinterface class, I added a treemap with a customized comparable that helps sort the aggregate donations based on the descending order of the donation amount. The treemap uses contributor name as key, and a list as values, the first element of the list is the number of times the contributor has donated, and the second element of the list is the total amount of their donations. After having everything sorted and stored in a treemap, I then iterate through the treemap and retrieve every entry and print them on the console. 

## Task 2.4. Contributor App caching

We first introduced a new variable `fundNameCache`, which was a HashMap object. The cache was keyed by the fund's ID and the value was the name of the fund. The variable was created to store the name of the fund once it was fetched from the REST API, so that the same data didn't have to be fetched again and again.

Then we modified the logic for retrieving the name of the fund in attemptLogin method. We first checked whether the `fundNameCache` already contained the name for the current fund's ID. If it did, we retrieved the fund name from the cache. If it didn't, we called the `getFundName` method to get the name from the API, and subsequently stored this value in `fundNameCache` for future use.

## Task 2.7. Organization App delete fund

This task consists of four parts, adding a `deleteFund` function in DataManager, displaying and providing navigation in `UserInterface`, writing Junit test cases and debugging, and implementing defensive programming. In the `DataManager` I added the deleteFund function which corresponds to the /deleteFund route, then in the UserInterface I added a deletedFund function as well which prompts the user to confirm for the deletion. In the `displayFund` method in UserInterface I also added prompts for users to choose the option of deleting the current fund. As for testing and defensive programming, I added a `DataManager_deletedFund_Test` class which resembles the `DataManagerRobustnessTest` class and passed all the test cases while gaining 100% coverage rate on the deleteFund method.

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
