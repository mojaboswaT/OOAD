import java.util.Optional;

public class AuthService {
    private final CSVStore store = new CSVStore();

    public Optional<Long> login(String username, String password) {
        var userOpt = store.findUserByUsername(username);
        if (userOpt.isEmpty()) return Optional.empty();
        var ua = userOpt.get();
        String hash = PasswordUtil.hash(password);
        if (ua.getPasswordHash().equals(hash)) return Optional.of(ua.getCustomerId());
        return Optional.empty();
    }

    public long register(Customer customer, String username, String password) {
        store.createCustomer(customer);
        UserAuth ua = new UserAuth();
        ua.setCustomerId(customer.getId());
        ua.setUsername(username);
        ua.setPasswordHash(PasswordUtil.hash(password));
        store.createUserAuth(ua);
        return customer.getId();
    }
}
