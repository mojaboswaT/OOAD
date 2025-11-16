import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class CloseAccountController {
    @FXML private ComboBox<Account> accountBox;
    @FXML private Label message;

    private final BankingService service = new BankingService();

    @FXML
    public void initialize() {
        accountBox.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getAccountNumber() + " - " + item.getType());
            }
        });
        accountBox.setButtonCell(new ListCell<>() { @Override protected void updateItem(Account item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.getAccountNumber()); } });
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
    public void handleClose() {
        try {
            Account acc = accountBox.getValue();
            if (acc == null) { message.setText("Select an account"); return; }
            service.closeAccount(acc.getId());
            message.setText("Closed account " + acc.getAccountNumber());
            refreshAccounts();
        } catch (Exception e) {
            message.setText("Failed: " + e.getMessage());
        }
    }
}
