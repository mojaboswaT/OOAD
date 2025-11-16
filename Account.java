import java.math.BigDecimal;

public abstract class Account {
    private long id;
    private String accountNumber;
    private long customerId;
    private BigDecimal balance = BigDecimal.ZERO;
    private AccountType type;
    private AccountClass accountClass;

    public abstract boolean canWithdraw(BigDecimal amount);

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public AccountType getType() { return type; }
    public void setType(AccountType type) { this.type = type; }

    public AccountClass getAccountClass() { return accountClass; }
    public void setAccountClass(AccountClass accountClass) { this.accountClass = accountClass; }
}
