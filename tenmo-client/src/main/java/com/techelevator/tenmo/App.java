package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.UserService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);
    NumberFormat currency = NumberFormat.getCurrencyInstance();

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        userService.setUser(currentUser);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        System.out.println("Your current balance is: ");
        BigDecimal balance = userService.getBalance();
        System.out.println(currency.format(balance));
	}

	private void viewTransferHistory() {
        List<Transfer> userTransfers = userService.getTransferHistory();
        System.out.println("-----------------------------");
        System.out.println("Transfers");
        System.out.println("ID      From/To        Amount");
        for(Transfer transfers : userTransfers) {
            System.out.print(transfers.getTransferId() + " \t");
            if(transfers.getAccountFrom() == currentUser.getUser().getId()) {
                System.out.print(transfers.getToName() + " \t \t");
            }
            else System.out.print(transfers.getFromName() + " \t \t");
            System.out.print(transfers.getAmount() + "\n");
        }
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
        List<User> tenmoUsers = userService.findAll();
        System.out.println("----------------------");
        System.out.println("Users");
        System.out.println("ID \t \t Name");
        System.out.println("----------------------");
        for (User user: tenmoUsers) {
            System.out.println(user.getId() + "\t" + user.getUsername());
        }
        System.out.println("-----------");
        boolean validSelection = false;
        int transferId = 0;
        while(validSelection == false) {
            transferId = consoleService.promptForInt("If you wish to abort this action, enter -1. Please select the user id you wish to send Bucks to: ");
            if (transferId == currentUser.getUser().getId()) {
                System.out.println("Invalid recipient, please enter a different id.");
                continue;
            }
            for(User users : tenmoUsers) {
                if(transferId == users.getId()) {
                    validSelection = true;
                    break;
                }
            }
            if (transferId == -1) {
                return;
            }
        }
        BigDecimal transferAmount = consoleService.promptForBigDecimal("Please enter the amount you wish to transfer: ");
        Transfer newTransfer = new Transfer();
        if(userService.getBalance().compareTo(transferAmount) > -1 && transferAmount.compareTo(BigDecimal.valueOf(0)) > -1) {
            newTransfer.setAccountFrom(currentUser.getUser().getId());
            newTransfer.setAccountTo((long) transferId);
            newTransfer.setStatusId(2);
            newTransfer.setAmount(transferAmount);
            newTransfer.setTypeId(2);
        }
        else {
            System.out.println("Invalid amount. Please enter a valid amount to transfer.");
            return;
        }
        boolean completed = false;
        completed = userService.transfer(newTransfer);
		if (completed == true) {
            System.out.println("Transfer completed.");
        }
        else System.out.println("Transfer has failed.");
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
