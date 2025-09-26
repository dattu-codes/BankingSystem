import com.bank.model.Account;
import com.bank.service.BankingService;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        BankingService service = new BankingService();
        Scanner scanner = new Scanner(System.in);
        Account currentAccount = null;

        while (true) {
            System.out.println("\n--- Online Banking System ---");
            System.out.println("1. Create new Account");
            System.out.println("2. Login");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Transaction History");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    service.createAccount(username, password);
                    break;
                case 2:
                    System.out.print("Enter username: ");
                    username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    password = scanner.nextLine();
                    currentAccount = service.login(username, password);
                    break;
                case 3:
                    if (currentAccount != null) {
                        System.out.print("Enter amount to deposit: ");
                        double amt = scanner.nextDouble();
                        service.deposit(currentAccount, amt);
                    } else {
                        System.out.println("Please login first!");
                    }
                    break;
                case 4:
                    if (currentAccount != null) {
                        System.out.print("Enter amount to withdraw: ");
                        double amt = scanner.nextDouble();
                        service.withdraw(currentAccount, amt);
                    } else {
                        System.out.println("Please login first!");
                    }
                    break;
                case 5:
                    if (currentAccount != null) {
                        service.printTransactionHistory(currentAccount);
                    } else {
                        System.out.println("Please login first!");
                    }
                    break;
                case 6:
                    System.out.println("Thank you !");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
