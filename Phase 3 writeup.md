# 1. Tasks

- **Task 3.1.** Organization App new user registration
- **Task 3.2.** Organization App change password
- **Task 3.3.** Organization App edit account information
- **Task 3.4.** Organization App make donation

# 2. Task Descriptions

## Task 3.1. Organization App new user registration
We first added a new Map instance variable `contributorNameCache` in the `DataManager` class. This Map was implemented as a HashMap to store the name of a contributor against their ID once fetched from the RESTful API. The cache was keyed by the contributor's ID and the value was the contributor's name. Then we also initialized `contributorNameCache` in the `DataManager` constructor. Finally, we changed the way the `attemptLogin` method retrieved the contributor's name. We first checked if `contributorNameCache` already contained the name for the current contributor's ID. If it did, we retrieved the contributor's name from the cache. If not, we called the `getContributorName` method to fetch the name from the API, and then stored this value in `contributorNameCache` for future reference.

## Task 3.2. Organization App change password
According to the DataManagerRobustnessTest.js, I updated DataManager.js to include error handling and exception handling for WebClient, attemptLogin, getContributorName and createFund. It handles cases where the WebClient is null, login or password is null or empty, the WebClient fails to connect to the server, the response from the server is null, the response from the server indicates an error, or the response from the server is malformed JSON.
For UserInterface.js, it displays meaningful error messages when encountering difficulties in accessing data through the DataManager.js. The user is informed about the error and given the option to retry the operation that caused the error. The modifications ensure that the user is provided with clear feedback and can take appropriate action when errors occur, improving the overall usability of the user interface.

## Task 3.3. Organization App edit account information

In the `displayFund()` method of the userinterface class, I added a treemap with a customized comparable that helps sort the aggregate donations based on the descending order of the donation amount. The treemap uses contributor name as key, and a list as values, the first element of the list is the number of times the contributor has donated, and the second element of the list is the total amount of their donations. After having everything sorted and stored in a treemap, I then iterate through the treemap and retrieve every entry and print them on the console. 

## Task 3.4. Organization App make donation

We first introduced a new variable `fundNameCache`, which was a HashMap object. The cache was keyed by the fund's ID and the value was the name of the fund. The variable was created to store the name of the fund once it was fetched from the REST API, so that the same data didn't have to be fetched again and again.

Then we modified the logic for retrieving the name of the fund in attemptLogin method. We first checked whether the `fundNameCache` already contained the name for the current fund's ID. If it did, we retrieved the fund name from the cache. If it didn't, we called the `getFundName` method to get the name from the API, and subsequently stored this value in `fundNameCache` for future use.

# 3. Bugs Found and Fixed
N/A

# 4. Team Contributions

- **Task 3.1.** *Huifang Ye*
- **Task 3.2.** *Huifang Ye, Ruxue Yan, Clara Zhang*
- **Task 3.3.** *Ruxue Yan*
- **Task 3.4.** *Clara Zhang*
