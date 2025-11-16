import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class HistoryController {
    @FXML private ComboBox<Account> accountBox;
    @FXML private TableView<TransactionRecord> table;
    @FXML private TableColumn<TransactionRecord, String> typeCol;
    @FXML private TableColumn<TransactionRecord, String> amountCol;
    @FXML private TableColumn<TransactionRecord, String> timeCol;
    @FXML private TableColumn<TransactionRecord, String> refCol;
    @FXML private TableColumn<TransactionRecord, String> balCol;
    @FXML private Label message;

    private final BankingService service = new BankingService();

    @FXML
    public void initialize() {
        accountBox.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getAccountNumber());
            }
        });
        accountBox.setButtonCell(new ListCell<>() { @Override protected void updateItem(Account item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.getAccountNumber()); } });

        typeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTxnType()));
        amountCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("BWP " + c.getValue().getAmount()));
        timeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getTimestamp())));
        refCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getReference()));
        balCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("BWP " + c.getValue().getBalanceAfter()));

        accountBox.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> refreshTable());
        refreshAccounts();
    }

    private void refreshAccounts() {
        try {
            List<Account> accounts = service.listAccounts(Session.getCurrentCustomer().getId());
            accountBox.setItems(FXCollections.observableList(accounts));
            if (!accounts.isEmpty()) accountBox.getSelectionModel().select(0);
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            Account acc = accountBox.getValue();
            if (acc == null) { table.setItems(FXCollections.observableArrayList()); return; }
            var list = service.history(acc.getId());
            table.setItems(FXCollections.observableList(list));
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }
}
