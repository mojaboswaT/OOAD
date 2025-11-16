import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

public class CSVStore {
    private final File customers = new File("customers.csv");
    private final File userAuth = new File("user_auth.csv");
    private final File accounts = new File("accounts.csv");
    private final File transactions = new File("transactions.csv");

    public CSVStore() {
        try {
            if (!customers.exists()) customers.createNewFile();
            if (!userAuth.exists()) userAuth.createNewFile();
            if (!accounts.exists()) accounts.createNewFile();
            if (!transactions.exists()) transactions.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized long nextId(File f) {
        long max = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length == 0) continue;
                max = Math.max(max, Long.parseLong(parts[0]));
            }
        } catch (Exception ignored) { }
        return max + 1;
    }

    // Customers
    public synchronized Customer createCustomer(Customer c) {
        c.setId(nextId(customers));
        writeLine(customers, String.join(",",
                String.valueOf(c.getId()),
                nz(c.getFirstName()), nz(c.getLastName()), nz(c.getAddress()), nz(c.getEmail()), nz(c.getPhone()),
                nz(c.getEmployerName()), nz(c.getEmployerAddress())
        ));
        return c;
    }

    public Optional<Customer> findCustomerById(long id) {
        try (BufferedReader br = new BufferedReader(new FileReader(customers, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (Long.parseLong(p[0]) == id) {
                    Customer c = new Customer();
                    c.setId(id);
                    c.setFirstName(p[1]); c.setLastName(p[2]); c.setAddress(p[3]); c.setEmail(p[4]); c.setPhone(p[5]);
                    c.setEmployerName(p[6]); c.setEmployerAddress(p[7]);
                    return Optional.of(c);
                }
            }
        } catch (IOException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    // UserAuth
    public synchronized UserAuth createUserAuth(UserAuth ua) {
        ua.setId(nextId(userAuth));
        writeLine(userAuth, String.join(",",
                String.valueOf(ua.getId()), String.valueOf(ua.getCustomerId()), nz(ua.getUsername()), nz(ua.getPasswordHash())
        ));
        return ua;
    }

    public Optional<UserAuth> findUserByUsername(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(userAuth, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (p[2].equals(username)) {
                    UserAuth ua = new UserAuth();
                    ua.setId(Long.parseLong(p[0]));
                    ua.setCustomerId(Long.parseLong(p[1]));
                    ua.setUsername(p[2]);
                    ua.setPasswordHash(p[3]);
                    return Optional.of(ua);
                }
            }
        } catch (IOException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    // Accounts
    public synchronized Account createAccount(Account a) {
        a.setId(nextId(accounts));
        writeLine(accounts, String.join(",",
                String.valueOf(a.getId()), String.valueOf(a.getCustomerId()), nz(a.getAccountNumber()),
                a.getBalance().toPlainString(), a.getType().name(), a.getAccountClass().name()
        ));
        return a;
    }

    public synchronized void updateAccountBalance(long id, BigDecimal newBalance) {
        List<String> lines = readAll(accounts);
        List<String> out = new ArrayList<>();
        for (String line : lines) {
            if (line.isBlank()) continue;
            String[] p = line.split(",", -1);
            if (Long.parseLong(p[0]) == id) {
                p[3] = newBalance.toPlainString();
                out.add(String.join(",", p));
            } else out.add(line);
        }
        writeAll(accounts, out);
    }

    public synchronized void deleteAccount(long id) {
        List<String> lines = readAll(accounts);
        List<String> out = new ArrayList<>();
        for (String line : lines) {
            if (line.isBlank()) continue;
            String[] p = line.split(",", -1);
            if (Long.parseLong(p[0]) != id) out.add(line);
        }
        writeAll(accounts, out);
    }

    public Optional<Account> findAccountById(long id) {
        try (BufferedReader br = new BufferedReader(new FileReader(accounts, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (Long.parseLong(p[0]) == id) {
                    AccountType type = AccountType.valueOf(p[4]);
                    Account a = switch (type) {
                        case SAVINGS -> new SavingsAccount();
                        case INVESTMENT -> new InvestmentAccount();
                        case CHEQUE -> new ChequeAccount();
                    };
                    a.setId(id);
                    a.setCustomerId(Long.parseLong(p[1]));
                    a.setAccountNumber(p[2]);
                    a.setBalance(new BigDecimal(p[3]));
                    a.setType(type);
                    a.setAccountClass(AccountClass.valueOf(p[5]));
                    return Optional.of(a);
                }
            }
        } catch (IOException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    public List<Account> listAccountsByCustomer(long customerId) {
        List<Account> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(accounts, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (Long.parseLong(p[1]) == customerId) {
                    AccountType type = AccountType.valueOf(p[4]);
                    Account a = switch (type) {
                        case SAVINGS -> new SavingsAccount();
                        case INVESTMENT -> new InvestmentAccount();
                        case CHEQUE -> new ChequeAccount();
                    };
                    a.setId(Long.parseLong(p[0]));
                    a.setCustomerId(customerId);
                    a.setAccountNumber(p[2]);
                    a.setBalance(new BigDecimal(p[3]));
                    a.setType(type);
                    a.setAccountClass(AccountClass.valueOf(p[5]));
                    result.add(a);
                }
            }
        } catch (IOException e) { throw new RuntimeException(e); }
        return result;
    }

    // Transactions
    public synchronized TransactionRecord addTransaction(TransactionRecord t) {
        t.setId(nextId(transactions));
        String ts = (t.getTimestamp() == null ? LocalDateTime.now() : t.getTimestamp()).toString();
        writeLine(transactions, String.join(",",
                String.valueOf(t.getId()), String.valueOf(t.getAccountId()), nz(t.getTxnType()),
                t.getAmount().toPlainString(), ts, nz(t.getReference()), t.getBalanceAfter().toPlainString()
        ));
        return t;
    }

    public List<TransactionRecord> listTransactionsByAccount(long accountId) {
        List<TransactionRecord> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(transactions, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (Long.parseLong(p[1]) == accountId) {
                    TransactionRecord t = new TransactionRecord();
                    t.setId(Long.parseLong(p[0]));
                    t.setAccountId(accountId);
                    t.setTxnType(p[2]);
                    t.setAmount(new BigDecimal(p[3]));
                    t.setTimestamp(LocalDateTime.parse(p[4]));
                    t.setReference(p[5]);
                    t.setBalanceAfter(new BigDecimal(p[6]));
                    result.add(t);
                }
            }
        } catch (IOException e) { throw new RuntimeException(e); }
        return result;
    }

    // Helpers
    private String nz(String s) { return s == null ? "" : s.replaceAll("\n", " ").replaceAll(",", " "); }

    private void writeLine(File f, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, StandardCharsets.UTF_8, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    private List<String> readAll(File f) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8))) {
            String line; while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) { throw new RuntimeException(e); }
        return lines;
    }

    private void writeAll(File f, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, StandardCharsets.UTF_8, false))) {
            for (String l : lines) { bw.write(l); bw.newLine(); }
        } catch (IOException e) { throw new RuntimeException(e); }
    }
}
