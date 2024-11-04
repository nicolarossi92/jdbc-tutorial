package it.mauluk92.jdbc.testutils;

import it.mauluk92.jdbc.testutils.annotation.JdbcSql;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Optional;

public class SqlCallback implements BeforeEachCallback, AfterEachCallback {

    private Connection conn;

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        this.conn.rollback();
        this.conn.close();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        this.conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        this.conn.setAutoCommit(false);
        context.getStore(ExtensionContext.Namespace.GLOBAL).put("conn", conn);
        if(context.getTestMethod().isPresent()){
            Optional<JdbcSql> ann = Optional.ofNullable(context.getTestMethod().get().getAnnotation(JdbcSql.class));
            if(ann.isPresent()){
                Resource res = new ClassPathResource(ann.get().value());
                String query = String.join("\n", Files.readAllLines(res.getFile().toPath()));
                conn.createStatement().execute(query);
            }
        }
    }
}
