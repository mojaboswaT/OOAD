import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class CreateAccountController {
    @FXML private ComboBox<AccountType> accountType;
    @FXML private ComboBox<AccountClass> accountClass;
    @FXML private TextField initialDeposit;
    @FXML private TextField employerName;
    @FXML private Label message;

    private final BankingService service = new BankingService();

    @FXML
    public void initialize() {
        accountType.setItems(FXCollections.observableArrayList(AccountType.values()));
        accountClass.setItems(FXCollections.observableArrayList(AccountClass.values()));
        accountType.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            employerName.setDisable(val != AccountType.CHEQUE);
        });
        employerName.setDisable(true);
    }

    @FXML
    public void handleCreate() {
        try {
            var type = accountType.getValue();
            var cls = accountClass.getValue();
            if (type == null || cls == null) {
                message.setText("Select account type and class");
                return;
            }
            BigDecimal deposit = initialDeposit.getText().isBlank() ? BigDecimal.ZERO : new BigDecimal(initialDeposit.getText());
            var acc = service.openAccount(Session.getCurrentCustomer().getId(), type, cls, deposit, employerName.getText());
            message.setText("Account created: " + acc.getAccountNumber());
        } catch (Exception e) {
            message.setText("Failed: " + e.getMessage());
        }
    }
}
