import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.List;

public class WithdrawController {
    @FXML private ComboBox<Account> accountBox;
    @FXML private TextField amountField;
    @FXML private TextField referenceField;
    @FXML private Label message;

    private final BankingService service = new BankingService();

    @FXML
    public void initialize() {
        accountBox.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getAccountNumber() + " - " + item.getType() + " (BWP " + item.getBalance() + ")");
            }
        });
        accountBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getAccountNumber() + " - " + item.getType());
            }
        });
        refreshAccounts();
    }

    private void refreshAccounts() {
        try {
            List<Account> accounts = service.listAccounts(Session.getCurrentCustomer().getId());
            accountBox.setItems(FXCollections.observableList(accounts));
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void handleWithdraw() {
        try {
            Account acc = accountBox.getValue();
            if (acc == null) { message.setText("Select an account"); return; }
            BigDecimal amt = new BigDecimal(amountField.getText());
            if (amt.compareTo(BigDecimal.ZERO) <= 0) { message.setText("Enter positive amount"); return; }
            service.withdraw(acc.getId(), amt, referenceField.getText());
            message.setText("Withdrew BWP " + amt + " from " + acc.getAccountNumber());
            refreshAccounts();
        } catch (Exception e) {
            message.setText("Failed: " + e.getMessage());
        }
    }
}
