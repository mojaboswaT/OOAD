import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.File;

public class MainMenuController {
    @FXML private StackPane contentArea;

    @FXML
    public void handleLogout() {
        SceneNavigator.navigate("login.fxml");
    }

    private void loadContent(String fxml) {
        try {
            Node node = FXMLLoader.load(new File(fxml).toURI().toURL());
            contentArea.getChildren().setAll(node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML public void navCreateAccount() { loadContent("create_account.fxml"); }
    @FXML public void navCloseAccount()  { loadContent("close_account.fxml"); }
    @FXML public void navViewAccounts()  { loadContent("view_accounts.fxml"); }
    @FXML public void navDeposit()       { loadContent("deposit.fxml"); }
    @FXML public void navWithdraw()      { loadContent("withdraw.fxml"); }
    @FXML public void navTransfer()      { loadContent("transfer.fxml"); }
    @FXML public void navHistory()       { loadContent("history.fxml"); }
}
