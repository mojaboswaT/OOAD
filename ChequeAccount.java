import java.math.BigDecimal;

public class ChequeAccount extends Account {
    @Override
    public boolean canWithdraw(BigDecimal amount) {
        return getBalance().compareTo(amount) >= 0;
    }
}
