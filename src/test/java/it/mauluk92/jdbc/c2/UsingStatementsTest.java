package it.mauluk92.jdbc.c2;

import it.mauluk92.jdbc.testutils.ConnectionParameterResolver;
import it.mauluk92.jdbc.testutils.SqlCallback;
import it.mauluk92.jdbc.testutils.annotation.JdbcSql;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class validates the rules about using JDBC statements
 */
@ExtendWith({ConnectionParameterResolver.class, SqlCallback.class})
public class UsingStatementsTest {

    /**
     * A Statement object is what sends your SQL statement to the DBMS.
     * You simply create a Statement object and then execute it, supplying the SQL
     * statement you want to send to the appropriate execute method
     */
    @Test
    @DisplayName("Creating JDBC statements")
    public void creatingJdbcStatement(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        // At this point the statement exists but it does not have a SQL statement to pass on to the DBMS
        // We need to supply that to the method we use to execute stmt. For example
        String updateString = "CREATE TABLE TABLE_C2(ID INT)";
        Assertions.assertEquals(0, stmt.executeUpdate(updateString));
    }

    /**
     * To get data from DBMS you can use an executeQuery method with SELECT statements
     */
    @Test
    @DisplayName("Executing Statements")
    @JdbcSql("c2/using_statements/executing_statements.sql")
    public void executingStatements(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        // The most used method for executing SQL statements is executeQuery, with SELECT statements
        ResultSet res = stmt.executeQuery("SELECT * FROM TABLE_C2");
        Assertions.assertTrue(res.next());
    }


}
