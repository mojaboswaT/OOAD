import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Optional;

public class LoginController {
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private Label loginMessage;

    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField address;
    @FXML private TextField email;
    @FXML private TextField phone;
    @FXML private TextField regUsername;
    @FXML private PasswordField regPassword;
    @FXML private Label registerMessage;

    private final AuthService authService = new AuthService();

    @FXML
    public void handleLogin() {
        String u = loginUsername.getText();
        String p = loginPassword.getText();
        Optional<Long> customerId = authService.login(u, p);
        if (customerId.isPresent()) {
            Customer c = new Customer();
            c.setId(customerId.get());
            Session.setCurrentCustomer(c);
            SceneNavigator.navigate("main_menu.fxml");
        } else {
            loginMessage.setText("Invalid credentials");
        }
    }

    @FXML
    public void handleRegister() {
        try {
            Customer c = new Customer();
            c.setFirstName(firstName.getText());
            c.setLastName(lastName.getText());
            c.setAddress(address.getText());
            c.setEmail(email.getText());
            c.setPhone(phone.getText());
            long id = authService.register(c, regUsername.getText(), regPassword.getText());
            registerMessage.setText("Registered successfully. Please login.");
        } catch (Exception ex) {
            registerMessage.setText("Registration failed: " + ex.getMessage());
        }
    }
}
