package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);
    private final RestTemplate restTemplate = new RestTemplate();

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        //removing optional cases from the menu selection process
        //     System.out.println("3: View your pending requests");
        System.out.println("3: Send TE bucks");
        //removing optional cases from the menu selection process
        //      System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    //new method
    public List<User> getOtherAccountUsers(User[] users, long currentUserId) {
        List<User> otherUsers = new ArrayList<>();

        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.println("ID          Name");
        System.out.println("-------------------------------------------");
        for(User user: users) {
            //removes current user from list
            if(user.getId() == currentUserId) {
                continue;
            }
            otherUsers.add(user);
            System.out.println(user.getId()+"        "+user.getUsername());
        }
        System.out.println("---------");

        return otherUsers;
    }

    public void getTransfers(Transfer[] transfers, Long accountId) {
        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.println("ID          From/To                 Amount");
        System.out.println("-------------------------------------------");
        for(Transfer transfer: transfers) {
            if(transfer.getAccountFrom().equals(accountId)) { //if user is sender

                String receiverUsername = restTemplate.getForObject("http://localhost:8080/username?accountid=" +transfer.getAccountTo(), String.class);
                System.out.println(transfer.getId()+"        To: "+receiverUsername+"          $"+transfer.getAmount());

            } else { //if user is receiver

                String senderUsername = restTemplate.getForObject("http://localhost:8080/username?accountid=" +transfer.getAccountFrom(), String.class);
                System.out.println(transfer.getId()+"        From: "+senderUsername+"          $"+transfer.getAmount());
            }
        }
        System.out.println("---------");

        //get valid transfer ID
        Transfer chosenTransfer = null;
        while(chosenTransfer == null) {
            int id = promptForInt("Please enter transfer ID to view details (0 to cancel):");

            if(id == 0) {
                return;
            }

            for(Transfer transfer: transfers) {
                if(transfer.getId() == id) {
                    chosenTransfer = transfer;
                    break;
                }
            }

            if(chosenTransfer == null) {
                System.out.println("Invalid selection");
            }
        }

        getTransferById(chosenTransfer);
    }

    public void getTransferById(Transfer transfer) {
        String sender = restTemplate.getForObject("http://localhost:8080/username?accountid=" +transfer.getAccountFrom(), String.class);
        String receiver = restTemplate.getForObject("http://localhost:8080/username?accountid=" +transfer.getAccountTo(), String.class);

        String type = restTemplate.getForObject("http://localhost:8080/type/" +transfer.getTransferTypeId(), String.class);
        String status = restTemplate.getForObject("http://localhost:8080/status/" +transfer.getTransferStatusId(), String.class);

        System.out.println("--------------------------------------------");
        System.out.println("Transfer Details");
        System.out.println("--------------------------------------------");
        System.out.println("Id: "+transfer.getId());
        System.out.println("From: " +sender);
        System.out.println("To: " +receiver);
        System.out.println("Type: " +type);
        System.out.println("Status: " +status);
        System.out.println("Amount: $" +transfer.getAmount());
    }

}
