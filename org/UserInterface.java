import java.util.*;

public class UserInterface {

	private DataManager dataManager;
	private Organization org;
	private static Scanner in = new Scanner(System.in);

	public UserInterface(DataManager dataManager, Organization org) {
		this.dataManager = dataManager;
		this.org = org;
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
			int option = in.nextInt();
			in.nextLine();
			if (option == 0) {
				createFund();
			} else {
				displayFund(option);
			}
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
			System.out.println("Press the Enter key to go back to the listing of funds");
			int option = in.nextInt();
			in.nextLine();

			if (option == 0) {
				deleteFund(fundNumber);
			}
		} catch (Exception e) {
			System.out.println("An error occurred while displaying the fund information. Please try again.");
			in.nextLine();
			displayFund(fundNumber);
		}
	}

	public static void main(String[] args) {
		DataManager ds = new DataManager(new WebClient("localhost", 3001));
		String login = args[0];
		String password = args[1];

		try {
			Organization org = ds.attemptLogin(login, password);
			if (org == null) {
				System.out.println("Login failed.");
			} else {
				UserInterface ui = new UserInterface(ds, org);
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
}