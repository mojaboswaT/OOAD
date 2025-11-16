import java.math.BigDecimal;

public class SavingsAccount extends Account {
    @Override
    public boolean canWithdraw(BigDecimal amount) {
        return false;
    }
}
