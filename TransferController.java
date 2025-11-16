import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.List;

public class TransferController {
    @FXML private ComboBox<Account> fromAccount;
    @FXML private ComboBox<Account> toAccount;
    @FXML private TextField amountField;
    @FXML private Label message;

    private final BankingService service = new BankingService();

    @FXML
    public void initialize() {
        var cellFactory = new javafx.util.Callback<ListView<Account>, ListCell<Account>>() {
            @Override public ListCell<Account> call(ListView<Account> lv) {
                return new ListCell<>() {
                    @Override protected void updateItem(Account item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : item.getAccountNumber() + " - " + item.getType() + " (BWP " + item.getBalance() + ")");
                    }
                };
            }
        };
        fromAccount.setCellFactory(cellFactory);
        toAccount.setCellFactory(cellFactory);
        fromAccount.setButtonCell(new ListCell<>() { @Override protected void updateItem(Account item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.getAccountNumber()); } });
        toAccount.setButtonCell(new ListCell<>() { @Override protected void updateItem(Account item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.getAccountNumber()); } });
        refreshAccounts();
    }

    private void refreshAccounts() {
        try {
            List<Account> accounts = service.listAccounts(Session.getCurrentCustomer().getId());
            fromAccount.setItems(FXCollections.observableList(accounts));
            toAccount.setItems(FXCollections.observableList(accounts));
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void handleTransfer() {
        try {
            Account from = fromAccount.getValue();
            Account to = toAccount.getValue();
            if (from == null || to == null) { message.setText("Select both accounts"); return; }
            BigDecimal amt = new BigDecimal(amountField.getText());
            if (amt.compareTo(BigDecimal.ZERO) <= 0) { message.setText("Enter positive amount"); return; }
            service.transfer(from.getId(), to.getId(), amt);
            message.setText("Transferred BWP " + amt + " from " + from.getAccountNumber() + " to " + to.getAccountNumber());
            refreshAccounts();
        } catch (Exception e) {
            message.setText("Failed: " + e.getMessage());
        }
    }
}
