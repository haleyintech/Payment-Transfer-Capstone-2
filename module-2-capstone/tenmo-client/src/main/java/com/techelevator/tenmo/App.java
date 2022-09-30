package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import io.cucumber.java.it.Data;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    private final RestTemplate restTemplate = new RestTemplate();


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
                System.out.println("Your current account balance is: " +getCurrentBalance());
            } else if (menuSelection == 2) {
                viewTransferHistory();
                //removing optional cases from the menu selection process
    //        } else if (menuSelection == 3) { viewPendingRequests();
            } else if (menuSelection == 3) {
                sendBucks();
                //removing optional cases from the menu selection process
                //       } else if (menuSelection == 5) { requestBucks();
            } else if (menuSelection == 0) {
                System.out.println("\n" +
                        "▀█▀ █░█ ▄▀█ █▄░█ █▄▀ █▀   █▀▀ █▀█ █▀█   █░█ █▀ █ █▄░█ █▀▀   ▀█▀ █▀▀ █▄░█ █▀▄▀█ █▀█ █\n" +
                        "░█░ █▀█ █▀█ █░▀█ █░█ ▄█   █▀░ █▄█ █▀▄   █▄█ ▄█ █ █░▀█ █▄█   ░█░ ██▄ █░▀█ █░▀░█ █▄█ ▄ !");
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private BigDecimal getCurrentBalance() {
        long userID = currentUser.getUser().getId();

        Account account = restTemplate.getForObject("http://localhost:8080/account?userid=" +userID, Account.class);

        return account.getBalance();
	}

	private void viewTransferHistory() {
        Account currentUserAccount = restTemplate.getForObject("http://localhost:8080/account?userid=" +currentUser.getUser().getId(), Account.class);

        Transfer[] transfers = restTemplate.getForObject("http://localhost:8080/transfers/" +currentUserAccount.getId(), Transfer[].class);

        consoleService.getTransfers(transfers, currentUserAccount.getId());





		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
        User[] users = restTemplate.getForObject("http://localhost:8080/users", User[].class);

        List<User> otherUsers = consoleService.getOtherAccountUsers(users, currentUser.getUser().getId());

        //chooses valid user to send money to
        User selectedUser = null;
        while(selectedUser == null) {
            int recipientID = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel):");

            if(recipientID == 0) return;

            for(User user: otherUsers) {
                if(user.getId() == recipientID) {
                    selectedUser = user;
                    break;
                }
            }

            if(selectedUser == null) {
                System.out.println("Invalid selection");
            }
        }

        //chooses valid amount of money to send
        BigDecimal sentAmount = null;
        while(sentAmount == null) {
            BigDecimal amount = consoleService.promptForBigDecimal("Enter amount:");

            //amount has to be less than or equal to balance and greater than 0
            if(amount.compareTo(getCurrentBalance()) <= 0 && amount.compareTo(BigDecimal.ZERO) > 0) {
                sentAmount = amount;
            }

            if(sentAmount == null) {
                System.out.println("Enter a valid amount for this transaction");
            }
        }


        Account currentUserAccount = restTemplate.getForObject("http://localhost:8080/account?userid=" +currentUser.getUser().getId(), Account.class);
        Account sentUserAccount = restTemplate.getForObject("http://localhost:8080/account?userid=" +selectedUser.getId(), Account.class);


        Long transactionId = restTemplate.getForObject("http://localhost:8080/send?amount="+sentAmount+"&sendid="+currentUserAccount.getId()+"&receiveid="+sentUserAccount.getId(), Long.class);

        if(transactionId != null) {
            System.out.println("Transaction was a success!");
        } else {
            System.out.println("Transaction error");
        }
    }

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
