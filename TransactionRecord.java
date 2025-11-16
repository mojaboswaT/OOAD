import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionRecord {
    private long id;
    private long accountId;
    private String txnType;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String reference;
    private BigDecimal balanceAfter;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getAccountId() { return accountId; }
    public void setAccountId(long accountId) { this.accountId = accountId; }
    public String getTxnType() { return txnType; }
    public void setTxnType(String txnType) { this.txnType = txnType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
}
