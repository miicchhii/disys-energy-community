package at.technikumwien.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DBCheck implements CommandLineRunner {
    private final DataSource dataSource;

    public DBCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
                System.out.println("✅ DB-Verbindung erfolgreich: " + conn.getMetaData().getURL());
            } else {
                System.err.println("❌ DB-Verbindung ungültig");
            }
        }
    }
}
