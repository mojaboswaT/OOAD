public class BankSystem {
    public static void main(String[] args) {
        // Create a customer
        Customer customer = new Customer("Kabo Mpho", "ID1234567", "kmpho@email.com", 
                                       "67 Raven st", "555-1234", "Male");

        // Create different types of accounts
        SavingsAccount savings = new SavingsAccount("Kabo Mpho", 1001, 5000.0, "Main Branch", 1234);
        InvestmentAccount investment = new InvestmentAccount("Kabo Mpho", 1002, 10000.0, "Main Branch", 2000.0);
        ChequeAccount cheque = new ChequeAccount("Kabo Mpho", 1003, 2500.0, "Main Branch", 1000.0);

        // Add accounts to customer
        customer.addAccount(savings);
        customer.addAccount(investment);
        customer.addAccount(cheque);

        // Test account operations
        savings.deposit(1000);
        savings.withdraw(500);
        savings.CalculateMonthlyInterest();

        investment.withdraw(8000);
        investment.interest();

        cheque.withdraw(3000); // This should work due to overdraft
        cheque.deposit(1000);

        // Display all accounts
        customer.displayAllAccounts();
    }
}