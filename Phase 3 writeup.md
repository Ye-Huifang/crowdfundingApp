# 1. Tasks

- **Task 3.1.** Organization App new user registration
- **Task 3.2.** Organization App change password
- **Task 3.3.** Organization App edit account information
- **Task 3.4.** Organization App make donation

# 2. Task Descriptions

## Task 3.1. Organization App new user registration
In `admin/api.js`, a new route `/createOrganization` was introduced to facilitate the creation of a new organization. For `org/DataManager.java`, the `attemptRegister` method was added, taking four parameters (`login`, `password`, `name`, `description`) to send a request to the server, creating a new organization and handling any arising errors. In `org/UserInterface.java`, a `register` method was developed to interact with the user, obtaining the necessary details for registration, calling `attemptRegister` from `DataManager`, and handling any exceptions that might occur during registration. These methods enabled new user registration within the Organization App.

## Task 3.2. Organization App change password
In the `admin/api.js`, a new endpoint `/updateOrganization` was added to update an organization's password. For `org/DataManager.java`, a new method `attemptChangePassword` was incorporated. This method sends a request to the server with the organization's ID and the new password, and handles any resulting errors. Within `org/UserInterface.java`, the `changePassword` method was devised. It interacts with the user to obtain the current and new password, uses the `attemptChangePassword` for password updates, and deals with potential exceptions. These modifications work in synergy to introduce a new feature in the Organization App that allows logged-in users to change their organization's password.

In the DataManager_attemptChangePassword_Test class, I created JUnit tests to assess the attemptChangePassword method of the DataManager class including a successful password change, invalid JSON, inability to connect to the server, improper server response, and the handling of null inputs. Each test case mocks the WebClient's response to simulate these conditions, and the assertions ensure the method behaves as expected.
## Task 3.3. Organization App edit account information
The updateOrganizationInfo method is added to DataManager to enable a logged-in user to modify the organization's account information. It performs defensive programming checks by throwing exceptions if the client object is null or the organization ID is null or empty. The method constructs a request to update the organization information and sends it to the server using the makeRequest method. Then the response is parsed as JSON, if it is null or not an instance of JSON Object it throws an IllegalStateException with the message "Malformed JSON received"; if the response status is "error", it retrieves the error message from the JSON and throws an IllegalStateException with the error message; if the response status is "success", it extracts the updated organization information from the JSON and creates a new Organization object with the updated details; if the response status is neither "success" nor "error", it returns null.  

For testing, I created unit tests to cover successful updates, failed updates, invalid JSON responses, null WebClient, null organization ID, inability to connect to the server, and JSON not being an object. The tests validated the expected behavior and exception messages when applicable.

## Task 3.4. Organization App make donation

We first introduced a new variable `fundNameCache`, which was a HashMap object. The cache was keyed by the fund's ID and the value was the name of the fund. The variable was created to store the name of the fund once it was fetched from the REST API, so that the same data didn't have to be fetched again and again.

Then we modified the logic for retrieving the name of the fund in attemptLogin method. We first checked whether the `fundNameCache` already contained the name for the current fund's ID. If it did, we retrieved the fund name from the cache. If it didn't, we called the `getFundName` method to get the name from the API, and subsequently stored this value in `fundNameCache` for future use.

Then in the userInterface, we provided the option of making donations when displaying funds. The user will have to input a valid contributor id for the donation and a numeric and non-negative value for the donation amount. Both fields have to be non-empty or else we will keep sending prompts for the user to retry and enter again. Once we are sure that both fields are valid and the contributor id is already in our database, we will forward the information to the datamanager and the datamanager will send a request to the server. After receiving response from the server, we will forward the donation back the userinterface and it'll add the donation to the donationlist following the fund that we chose previously. During this process, any error occurred will be followed by a warning message and a prompt to adjust the input.

# 3. Bugs Found and Fixed
N/A

# 4. Team Contributions

- **Task 3.1.** *Huifang Ye*
- **Task 3.2.** *Huifang Ye, Ruxue Yan, Clara Zhang*
- **Task 3.3.** *Ruxue Yan*
- **Task 3.4.** *Clara Zhang*
