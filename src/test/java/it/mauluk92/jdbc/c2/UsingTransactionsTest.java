package it.mauluk92.jdbc.c2;

import it.mauluk92.jdbc.testutils.ConnectionParameterResolver;
import it.mauluk92.jdbc.testutils.SqlCallback;
import it.mauluk92.jdbc.testutils.annotation.JdbcSql;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.*;

/**
 * This class contains tests to validate rules about rollbacks, commit and transactions
 */
@ExtendWith({ConnectionParameterResolver.class, SqlCallback.class})
public class UsingTransactionsTest {
    /**
     * When a connection is created, it is in auto-commit mode.
     * This means that each individual SQL statements is treated as a transaction and will
     * automatically be committed right after its execution.
     * The way to allow two or more statements to be grouped into a transaction is to disable
     * auto-commit mode.
     */
    @Test
    @DisplayName("disabling autocommit mode")
    public void disablingAutoCommitMode(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
        Assertions.assertFalse(conn.getAutoCommit());
    }

    /**
     * All statements executed after the previous call to method commit will
     * be included in the current transaction and will be committed together as a unit
     */
    @Test
    @DisplayName("committing a transaction")
    @JdbcSql("c2/using_transactions/committing_a_transaction.sql")
    public void committingATransaction(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
        Statement stmt1 = conn.createStatement();
        Statement stmt2 = conn.createStatement();
        stmt1.executeUpdate("UPDATE TABLE_C2 SET NAME = 'NAME_UPDATED' WHERE ID = 1");
        stmt2.executeUpdate("UPDATE TABLE_C2 SET NAME = 'NAME_UPDATED' WHERE ID = 2");

        conn.commit();
    }

    /**
     * Aborts a transaction and returns any values that were modified to the values they had at
     * the beginning of the transaction. If you are trying to execute one or more statements in a transaction
     * and get a SQLException, you should call the rollback method to abort the transaction and undo any change
     */
    @Test
    @DisplayName("roll back a transaction")
    @JdbcSql("c2/using_transactions/rollback_a_transaction.sql")
    public void rollbackATransaction(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("UPDATE TABLE_C2 SET NAME = 'NAME_UPDATED' WHERE NAME= 'NAME_1'");
        ResultSet rs = stmt.executeQuery("SELECT ID, NAME FROM TABLE_C2");
        rs.next();
        Assertions.assertEquals("NAME_UPDATED", rs.getString("NAME"));
        conn.rollback();
        ResultSet rsAfterRollBack = conn.createStatement().executeQuery("SELECT ID, NAME FROM TABLE_C2");
        rsAfterRollBack.next();
        Assertions.assertNotEquals("NAME_UPDATED", rsAfterRollBack.getString("NAME"));
    }
}
