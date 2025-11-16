import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.List;

public class ViewAccountsController {
    @FXML private ListView<Account> accountsList;
    @FXML private Label message;

    private final BankingService service = new BankingService();

    @FXML
    public void initialize() {
        accountsList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.getAccountNumber() + " - " + item.getType() + " (" + item.getAccountClass() + ")  BWP " + item.getBalance());
            }
        });
        refresh();
    }

    private void refresh() {
        try {
            List<Account> accounts = service.listAccounts(Session.getCurrentCustomer().getId());
            accountsList.setItems(FXCollections.observableList(accounts));
            message.setText(accounts.isEmpty() ? "No accounts" : "");
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }
}
