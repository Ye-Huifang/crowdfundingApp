import java.util.*;

public class UserInterface {

	private DataManager dataManager;
	private Organization org;
	private String loggedInUser;
	private static Scanner in = new Scanner(System.in);

	public UserInterface(DataManager dataManager, Organization org, String loggedInUser) {
		this.dataManager = dataManager;
		this.org = org;
		this.loggedInUser = loggedInUser;
	}

	public void start() {
		while (true) {
			System.out.println("\n\n");
			if (org.getFunds().size() > 0) {
				System.out.println("There are " + org.getFunds().size() + " funds in this organization:");
				int count = 1;
				for (Fund f : org.getFunds()) {
					System.out.println(count + ": " + f.getName());
					count++;
				}
				System.out.println("Enter the fund number to see more information.");
			}
			System.out.println("Enter 0 to create a new fund");
			System.out.println("Enter c to change password");
			String option = in.nextLine();
			try {
				int optionNum = Integer.parseInt(option);
				if (optionNum == 0) {
					createFund();
				} else {
					displayFund(optionNum);
				}
			} catch (NumberFormatException e) {
				if (option.equals("c")) {
					changePassword();
				} else if (option.equals("e")) { // Handle edit account info option
					editAccountInfo();
				}
			}
		}
	}

	public void changePassword() {
		System.out.println("Enter your current password");
		String password = in.nextLine();
		while (password == null || password.isEmpty()) {
			System.out.println("Password cannot be empty");
			password = in.nextLine();
		}
		Organization org;
		try {
			org = dataManager.attemptLogin(loggedInUser, password);
			if (org == null) {
				return;
			}
		} catch (Exception e) {
			System.out.println("The input password is incorrect.");
			return;
		}
		System.out.println("Enter new password:");
		String newPassword = in.nextLine();
		while (newPassword == null || newPassword.isEmpty()) {
			System.out.println("Password cannot be empty");
			newPassword = in.nextLine();
		}
		System.out.println("Enter new password again:");
		if (!newPassword.equals(in.nextLine())) {
			System.out.println("The password you entered does not match the first one.");
			return;
		}
		try {
			dataManager.attemptChangePassword(org, newPassword);
		} catch (IllegalStateException e) {
			if (e.getMessage().equals("Cannot connect to server")) {
				System.out.println("Error in communicating with the server. Please try again.");
			} else {
				System.out.println("Password change failed.");
			}
		}
	}

	public void editAccountInfo() {
		System.out.println("Enter your current password:");
		String password = in.nextLine();

		try {
			Organization authenticatedOrg = dataManager.attemptLogin(loggedInUser, password);

			if (authenticatedOrg == null) {
				System.out.println("Incorrect password. Please try again.");
				return;
			}

			System.out.println("Enter the new organization name (leave blank to keep the current name):");
			String newOrgName = in.nextLine();
			System.out.println("Enter the new organization description (leave blank to keep the current description):");
			String newOrgDescription = in.nextLine();

			Organization updatedOrg = dataManager.updateOrganizationInfo(org.getId(), newOrgName, newOrgDescription);

			if (updatedOrg != null) {
				org = updatedOrg;
				System.out.println("Account information updated successfully!");
			} else {
				System.out.println("Account information update failed.");
			}
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			System.out.println("Error: Please try again.");
		}
	}

	public void createFund() {
		try {
			System.out.print("Enter the fund name: ");
			String name = in.nextLine().trim();
			System.out.print("Enter the fund description: ");
			String description = in.nextLine().trim();
			System.out.print("Enter the fund target: ");
			long target = in.nextInt();
			in.nextLine();

			Fund fund = dataManager.createFund(org.getId(), name, description, target);
			org.getFunds().add(fund);
		} catch (Exception e) {
			System.out.println("An error occurred while creating the fund. Please try again.");
			in.nextLine();
			createFund();
		}
	}

	public void deleteFund(int fundNumber) {
		try {
			System.out.print("Are you sure you want to delete this fund? ");
			System.out.print("Press 1 to confirm ");
			int option = in.nextInt();
			in.nextLine();
			if (option == 1) {
				Fund fund = org.getFunds().get(fundNumber - 1);
				Fund removeFund = dataManager.deleteFund(fund.getId());
				List<Fund> newlist = org.getFunds();
				newlist.remove(fundNumber - 1);
			} else {
				displayFund(fundNumber);
			}
		} catch (Exception e) {
			System.out.println("An error occurred while deleting the fund. Please try again.");
			in.nextLine();
			displayFund(fundNumber);
		}
	}

	public void displayFund(int fundNumber) {
		try {
			Fund fund = org.getFunds().get(fundNumber - 1);
			System.out.println("\n\n");
			System.out.println("Here is information about this fund:");
			System.out.println("Name: " + fund.getName());
			System.out.println("Description: " + fund.getDescription());
			System.out.println("Target: $" + fund.getTarget());

			displayAllDonations(fundNumber);
			System.out.println("Enter 0 to delete this fund");
			System.out.println("Enter 1 to go back to the listing of funds");
			System.out.println("Enter 2 to look at donations grouped by contributors");
			System.out.println("Enter 3 to make donations");
			int option = in.nextInt();
			in.nextLine();

			if (option == 0) {
				deleteFund(fundNumber); 
			} else if (option == 2) {
				displayAggregateDonations(fundNumber);
			} else if (option == 3) {
				makeDonation(fundNumber);
			}
		} catch (Exception e) {
			System.out.println("An error occurred while displaying the fund information. Please try again.");
			in.nextLine();
			displayFund(fundNumber);
		}
	}
	
	private void displayAllDonations(int fundNumber) {
		Fund fund = org.getFunds().get(fundNumber - 1);
		System.out.println("\n\n");
		System.out.println("Here is information about this fund:");
		System.out.println("Name: " + fund.getName());
		System.out.println("Description: " + fund.getDescription());
		System.out.println("Target: $" + fund.getTarget());
		List<Donation> donations = fund.getDonations();
		System.out.println("Number of donations: " + donations.size());
		for (Donation donation : donations) {
			System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + donation.getDate());
		}
		
	}
	
	private void displayAggregateDonations(int fundNumber) {
		
		Fund fund = org.getFunds().get(fundNumber - 1);
		System.out.println("\n\n");
		System.out.println("Here is information about this fund:");
		System.out.println("Name: " + fund.getName());
		System.out.println("Description: " + fund.getDescription());
		System.out.println("Target: $" + fund.getTarget());
		List<Donation> donations = fund.getDonations();
		System.out.println("Number of donations: " + donations.size());
		long totalDonations = 0;
		TreeMap<String, List<Integer>> treeMap = new TreeMap<>();

		for (Donation donation : donations) {
			totalDonations += donation.getAmount();
			String name = donation.getContributorName();

			List<Integer> prevList = treeMap.getOrDefault(name, new ArrayList<>());
			int count = prevList.isEmpty() ? 1 : prevList.get(0) + 1;
			int totalAmount = prevList.isEmpty() ? (int) donation.getAmount() : prevList.get(1) + (int) donation.getAmount();

			List<Integer> newList = new ArrayList<>();
			newList.add(count);
			newList.add(totalAmount);
			treeMap.put(name, newList); 
		}

		TreeMap<String, List<Integer>> sortedTreeMap = new TreeMap<>((key1, key2) -> {
			List<Integer> list1 = treeMap.get(key1);
			List<Integer> list2 = treeMap.get(key2);
			return list2.get(1) - list1.get(1);
		});
		sortedTreeMap.putAll(treeMap);

		for (Map.Entry<String, List<Integer>> entry : sortedTreeMap.entrySet()) {
			String name = entry.getKey();
			List<Integer> values = entry.getValue();
			int count = values.get(0);
			int totalAmount = values.get(1);
			System.out.println("* " + name + ", " + count + " donations, $" + totalAmount + " total");
		}
		int percentage = (int) (totalDonations * 100.0 / fund.getTarget());
		System.out.println("Total donation amount: $" + totalDonations + " (" + percentage + "% of target)");
		System.out.println("Enter 0 to delete this fund");
		System.out.println("Enter 1 to go back to the listing of funds");
		System.out.println("Enter 2 to look at each donation");
		System.out.println("Enter 3 to make donations");
		int option = in.nextInt();
		in.nextLine();

		if (option == 0) {
			deleteFund(fundNumber); 
		} else if (option == 2) {
			displayFund(fundNumber);
		} else if (option == 3) {
			makeDonation(fundNumber);
		}
	
	}
	
	
	private void makeDonation(int fundNumber) {
		Fund fund = org.getFunds().get(fundNumber - 1);
		System.out.println("contributor id:");
		String contributorid = in.nextLine();
		while (contributorid.isEmpty() || !isContributorIdValid(contributorid, fundNumber)) {
			System.out.println("Invalid contributor id, retry please");

			contributorid = in.nextLine();
		} 

		System.out.println("amount:");
		String donationamount = in.nextLine();
		
		while(donationamount.isEmpty() || !isAmountValid(donationamount) || Integer.parseInt(donationamount) < 0) {
			System.out.println("Invalid amount, retry please");
			
			donationamount = in.nextLine();
		}
		System.out.println("Enter 1 to submit");
		int option = in.nextInt();
		if(option == 1) {
			
			String fundid = fund.getId();
			try{
				Donation d = dataManager.makeDonation(contributorid, fundid, donationamount);
				if (d != null) {
					List<Donation> donations = fund.getDonations();
					donations.add(d);
					fund.setDonations(donations);
					displayFund(fundNumber);
				}
			} catch (Exception e){
				System.out.println(e.getMessage());
	            System.out.println("Error: Please try to again");
	            makeDonation(fundNumber);
			}
			
		}

	}
	
	private boolean isAmountValid(String donationamount) {
		for (int i = 0; i < donationamount.length(); i++) {
			if(!Character.isDigit(donationamount.charAt(i))){
				return false;
			}
		} 
		return true;
	}
	
	private boolean isContributorIdValid(String id, int fundNumber) {
	    try {
	        String name = dataManager.getContributorName(id);
	        return name != null;
	    } catch (Exception e) {
            return false;
	    }
	}
	
	private static int promptBeforeLogin() {
		while (true) {
			System.out.println("Select from the below options (Enter 1 or 2): \n1. Login\n2. Register");
			int option;
			try {
				option = Integer.parseInt(in.nextLine());
			} catch (Exception e) {
				System.out.println("Invalid input.");
				continue;
			}
			if (option != 1 && option != 2) {
				System.out.println("You should input either 1 or 2.");
				continue;
			}
			return option;
		}
	}

	private static void login(String[] args, DataManager ds) {
		try {
			System.out.println("Login user:");
			String login = in.nextLine();
			System.out.println("Password:");
			String password = in.nextLine();
			Organization org = ds.attemptLogin(login, password);
			if (org == null) {
				System.out.println("Login failed.");
			} else {
				UserInterface ui = new UserInterface(ds, org, login);
				ui.start();
			}
		} catch (IllegalStateException e) {
			if (e.getMessage().equals("Cannot connect to server")) {
				System.out.println("Error in communicating with the server. Please try again.");
			} else {
				System.out.println("Login failed.");
			}
			in.nextLine();
			main(args);
		}
	}

	private static void register(String[] args, DataManager ds) {
		try {
			System.out.println("Register login name:");
			String login = in.nextLine();
			System.out.println("Password:");
			String password = in.nextLine();
			System.out.println("Organization name:");
			String organizationName = in.nextLine();
			System.out.println("Organization Description:");
			String organizationDescription = in.nextLine();
			Organization org = ds.attemptRegister(login, password, organizationName, organizationDescription);
			if (org == null) {
				System.out.println("Register failed.");
			} else {
				UserInterface ui = new UserInterface(ds, org, login);
				ui.start();
			}
		} catch (IllegalStateException e) {
			if (e.getMessage() != null && e.getMessage().equals("Cannot connect to server")) {
				System.out.println("Error in communicating with the server. Please try again.");
			} else {
				System.out.println("Register failed.");
			}
			main(args);
		}
	}

	public static void main(String[] args) {
		DataManager ds = new DataManager(new WebClient("localhost", 3001));
		int option = promptBeforeLogin();
		if (option == 1) {
			login(args, ds);
		} else if (option == 2) {
			register(args, ds);
		}
	}
}