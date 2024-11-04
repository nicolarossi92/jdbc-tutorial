package it.mauluk92.jdbc.c2;

import it.mauluk92.jdbc.testutils.ConnectionParameterResolver;
import it.mauluk92.jdbc.testutils.SqlCallback;
import it.mauluk92.jdbc.testutils.annotation.JdbcSql;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class contains tests to validate rules about statements and relative operations
 */
@ExtendWith({ConnectionParameterResolver.class, SqlCallback.class})
public class UsingPreparedStatementsTest {
    /**
     * Prepared statements object are created with a sql string, which
     * will be sent to the DBMS right away, where it will be compiled. As a
     * result, the PreparedStatement is executed, the DBMS can just run the
     * PreparedStatement's SQL statement without having to compile it first
     */
    @Test
    @DisplayName("Creating prepared statements")
    @JdbcSql("c2/using_prepared_statements/creating_prepared_statement.sql")
    public void creatingPreparedStatements(Connection conn) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(
                "UPDATE TABLE_C2 SET NAME= ? WHERE ID = ?"
        );
        Assertions.assertNotNull(preparedStatement);
    }

    /**
     * You will need to supply.-< values to be used in place of the question mark placeholders, if there are any,
     * before you can execute a PreparedStatement object. You do this by calling one of the setter
     * methods defined in the interface PreparedStatement
     */
    @Test
    @DisplayName("Supplying values to the prepared statement")
    @JdbcSql("c2/using_prepared_statements/supplying_values_prepared_statements.sql")
    public void supplyingValueToThePreparedStatement(Connection conn) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(
                "UPDATE TABLE_C2 SET NAME= ? WHERE ID = ?"
        );
        preparedStatement.setString(1, "NAME_1");
        preparedStatement.setInt(2, 1);
        Assertions.assertNotEquals(0,preparedStatement.executeUpdate());
    }

    /**
     * The return value of executeUpdate method indicates how many rows
     * were updated in a table.
     */
    @Test
    @DisplayName("Return values from executeUpdate")
    @JdbcSql("c2/using_prepared_statements/return_values_execute_update.sql")
    public void returnValuesExecuteUpdate(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        String sql = "UPDATE TABLE_C2 SET NAME = 'NAME_2' WHERE NAME = 'NAME_1'";
        Assertions.assertEquals(2, statement.executeUpdate(sql));
    }



}
