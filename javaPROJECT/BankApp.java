import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * BankApp.java
 * Simple console banking application for OOP assignment.
 *
 * Usage:
 *  javac BankApp.java
 *  java BankApp
 *
 * Single-file implementation: public class BankApp contains main.
 */

public class BankApp {
    private static Scanner scanner = new Scanner(System.in);
    // store accounts by account number
    private static Map<String, Account> accounts = new HashMap<>();
    // track the 7-digit unique portions already used (or full account numbers)
    private static Set<String> usedAccountNumbers = new HashSet<>();
    private static final String BANK_PREFIX = "4003772"; // 7 chars
    private static Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Welcome to the Simple OOP Bank App");
        int choice;
        do {
            showMenu();
            choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> createAccount();
                case 2 -> depositToAccount();
                case 3 -> withdrawFromAccount();
                case 4 -> showTransactionHistory();
                case 5 -> showAccountInfo();
                case 6 -> listAllAccounts();
                case 0 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid option. Try again.");
            }
        } while (choice != 0);
    }

    private static void showMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Create new account (Savings or Current)");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Show transaction details for an account");
        System.out.println("5. Show account information");
        System.out.println("6. List all accounts (brief)");
        System.out.println("0. Exit");
    }

    private static void createAccount() {
        System.out.println("\n--- Create Account ---");
        System.out.print("Account holder's name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        String typeInput;
        do {
            System.out.print("Account type (S for Savings / C for Current): ");
            typeInput = scanner.nextLine().trim().toUpperCase();
        } while (!typeInput.equals("S") && !typeInput.equals("C"));

        char type = typeInput.charAt(0);
        String accountNumber = generateUniqueAccountNumber(type);

        Account acc;
        if (type == 'S') acc = new SavingsAccount(name, accountNumber);
        else acc = new CurrentAccount(name, accountNumber);

        accounts.put(accountNumber, acc);
        usedAccountNumbers.add(accountNumber);

        System.out.println("Account created successfully!");
        System.out.println("Account Holder: " + name);
        System.out.println("Account Number: " + accountNumber);
        System.out.printf("Account Balance: %.2f%n", acc.getBalance());
    }

    // Generates unique account number with BANK_PREFIX + 7-digit unique + S/C
    private static String generateUniqueAccountNumber(char type) {
        String accNum;
        do {
            int unique7 = random.nextInt(10_000_000); // 0 to 9,999,999
            String unique7Str = String.format("%07d", unique7); // ensure 7 digits with leading zeros
            accNum = BANK_PREFIX + unique7Str + type;
        } while (usedAccountNumbers.contains(accNum));
        return accNum;
    }

    private static void depositToAccount() {
        System.out.println("\n--- Deposit ---");
        String accNumber = promptAccNumber();
        Account acc = accounts.get(accNumber);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }

        double amount = readDouble("Enter deposit amount: ");
        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }
        acc.deposit(amount);
        System.out.printf("Deposit successful. New balance: %.2f%n", acc.getBalance());
        // Show last transaction details
        System.out.println("Last transaction:");
        System.out.println(acc.getLastTransaction());
    }

    private static void withdrawFromAccount() {
        System.out.println("\n--- Withdraw ---");
        String accNumber = promptAccNumber();
        Account acc = accounts.get(accNumber);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }

        double amount = readDouble("Enter withdrawal amount: ");
        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }
        boolean success = acc.withdraw(amount);
        if (success) {
            System.out.printf("Withdrawal successful. New balance: %.2f%n", acc.getBalance());
            System.out.println("Last transaction:");
            System.out.println(acc.getLastTransaction());
        } else {
            System.out.println("Withdrawal failed: insufficient funds.");
        }
    }

    private static void showTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        String accNumber = promptAccNumber();
        Account acc = accounts.get(accNumber);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }
        acc.printTransactions();
    }

    private static void showAccountInfo() {
        System.out.println("\n--- Account Information ---");
        String accNumber = promptAccNumber();
        Account acc = accounts.get(accNumber);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }
        System.out.println(acc); // prints name, number, balance
    }

    private static void listAllAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts yet.");
            return;
        }
        System.out.println("\nAll accounts (brief):");
        for (Account a : accounts.values()) {
            System.out.printf("%s | %s | %.2f%n", a.getAccountHolderName(), a.getAccountNumber(), a.getBalance());
        }
    }

    private static String promptAccNumber() {
        System.out.print("Enter full account number (15 chars, e.g. 4003772xxxxxxxS/C): ");
        return scanner.nextLine().trim();
    }

    // helper read int
    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    // helper read double
    private static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = scanner.nextLine().trim();
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}

/* ---------------------------
   Account class hierarchy
   --------------------------- */

abstract class Account {
    private String accountHolderName;
    private String accountNumber; // full 15-char account number
    private double balance;
    private List<Transaction> transactions;

    public Account(String name, String accNumber) {
        this.accountHolderName = name;
        this.accountNumber = accNumber;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    // deposit
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
        balance += amount;
        addTransaction("DEPOSIT", amount);
    }

    // withdraw; returns true if success
    public boolean withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
        if (amount > balance) return false;
        balance -= amount;
        addTransaction("WITHDRAWAL", amount);
        return true;
    }

    private void addTransaction(String type, double amount) {
        Transaction t = new Transaction(type, amount, balance);
        transactions.add(t);
    }

    public Transaction getLastTransaction() {
        if (transactions.isEmpty()) return null;
        return transactions.get(transactions.size()-1);
    }

    public void printTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet for this account.");
            return;
        }
        System.out.println("Transactions for account " + accountNumber + ":");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }

    @Override
    public String toString() {
        return String.format("Account Holder: %s%nAccount Number: %s%nAccount Balance: %.2f",
                accountHolderName, accountNumber, balance);
    }
}

class SavingsAccount extends Account {
    public SavingsAccount(String name, String accNumber) {
        super(name, accNumber);
    }

    // add savings-specific behavior if needed
}

class CurrentAccount extends Account {
    public CurrentAccount(String name, String accNumber) {
        super(name, accNumber);
    }

    // add current-specific behavior if needed
}

/* Transaction class */
class Transaction {
    private String type; // DEPOSIT or WITHDRAWAL
    private double amount;
    private double balanceAfter;
    private LocalDateTime timestamp;
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %.2f | Balance after: %.2f",
                timestamp.format(fmt), type, amount, balanceAfter);
    }
}
