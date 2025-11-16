public class UserAuth {
    private long id;
    private long customerId;
    private String username;
    private String passwordHash;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
