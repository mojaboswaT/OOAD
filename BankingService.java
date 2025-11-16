import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BankingService {
    private final CSVStore store = new CSVStore();

    public Account openAccount(long customerId, AccountType type, AccountClass cls, BigDecimal initialDeposit, String employerNamePresent) {
        if (type == AccountType.INVESTMENT && (initialDeposit == null || initialDeposit.compareTo(new BigDecimal("500.00")) < 0)) {
            throw new IllegalArgumentException("Investment account needs at least BWP 500.00 initial deposit");
        }
        if (type == AccountType.CHEQUE && (employerNamePresent == null || employerNamePresent.isBlank())) {
            throw new IllegalArgumentException("Cheque account requires employment information");
        }
        Account a = switch (type) {
            case SAVINGS -> new SavingsAccount();
            case INVESTMENT -> new InvestmentAccount();
            case CHEQUE -> new ChequeAccount();
        };
        a.setCustomerId(customerId);
        a.setType(type);
        a.setAccountClass(cls);
        a.setAccountNumber(generateAccountNumber());
        a.setBalance(initialDeposit == null ? BigDecimal.ZERO : initialDeposit);
        store.createAccount(a);
        if (initialDeposit != null && initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            recordTxn(a, "DEPOSIT", initialDeposit, "Initial deposit");
        }
        return a;
    }

    public void closeAccount(long accountId) {
        store.deleteAccount(accountId);
    }

    public List<Account> listAccounts(long customerId) {
        return store.listAccountsByCustomer(customerId);
    }

    public List<TransactionRecord> history(long accountId) {
        return store.listTransactionsByAccount(accountId);
    }

    public void deposit(long accountId, BigDecimal amount, String reference) {
        var a = store.findAccountById(accountId).orElseThrow();
        a.setBalance(a.getBalance().add(amount));
        store.updateAccountBalance(a.getId(), a.getBalance());
        recordTxn(a, "DEPOSIT", amount, reference);
    }

    public void withdraw(long accountId, BigDecimal amount, String reference) {
        var aOpt = store.findAccountById(accountId);
        if (aOpt.isEmpty()) throw new IllegalArgumentException("Account not found");
        var a = aOpt.get();
        if (!a.canWithdraw(amount)) throw new IllegalArgumentException("Withdrawal not allowed or insufficient funds");
        a.setBalance(a.getBalance().subtract(amount));
        store.updateAccountBalance(a.getId(), a.getBalance());
        recordTxn(a, "WITHDRAW", amount.negate(), reference);
    }

    public void transfer(long fromAccountId, long toAccountId, BigDecimal amount) {
        if (fromAccountId == toAccountId) throw new IllegalArgumentException("Cannot transfer to same account");
        var from = store.findAccountById(fromAccountId).orElseThrow();
        var to = store.findAccountById(toAccountId).orElseThrow();
        if (!from.canWithdraw(amount)) throw new IllegalArgumentException("Insufficient funds or withdrawal not allowed");
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        store.updateAccountBalance(from.getId(), from.getBalance());
        store.updateAccountBalance(to.getId(), to.getBalance());
        recordTxn(from, "TRANSFER_OUT", amount.negate(), "Transfer to " + to.getAccountNumber());
        recordTxn(to, "TRANSFER_IN", amount, "Transfer from " + from.getAccountNumber());
    }

    public void applyMonthlyInterest(long accountId) {
        var a = store.findAccountById(accountId).orElseThrow();
        BigDecimal rate;
        if (a.getType() == AccountType.INVESTMENT) rate = new BigDecimal("0.05");
        else if (a.getType() == AccountType.SAVINGS) rate = new BigDecimal("0.0005");
        else return;
        BigDecimal interest = a.getBalance().multiply(rate);
        a.setBalance(a.getBalance().add(interest));
        store.updateAccountBalance(a.getId(), a.getBalance());
        recordTxn(a, "INTEREST", interest, "Monthly interest");
    }

    private void recordTxn(Account a, String type, BigDecimal amount, String ref) {
        TransactionRecord t = new TransactionRecord();
        t.setAccountId(a.getId());
        t.setTxnType(type);
        t.setAmount(amount);
        t.setReference(ref);
        t.setBalanceAfter(a.getBalance());
        store.addTransaction(t);
    }

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
