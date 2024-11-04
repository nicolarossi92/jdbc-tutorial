package it.mauluk92.jdbc.c1;

import it.mauluk92.jdbc.testutils.ConnectionParameterResolver;
import it.mauluk92.jdbc.testutils.SqlCallback;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.*;

/**
 * This class contains tests to validate rules about the fundamentals of getting a connection
 * and basics of JDBC api
 */
@ExtendWith({ConnectionParameterResolver.class, SqlCallback.class})
public class WhatJdbcAPIDoTest {
    /**
     * Fundamentally, jdbc gets its own Connection object from a DriverManager
     * and this suffices to establish the connection with the DBMS
     */
    @Test
    @DisplayName("Jdbc API allow to establish a connection with a DBMS")
    public void establishAConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        Assertions.assertTrue(connection.isValid(1));
    }

    /**
     * The Jdbc API allows to execute queries and updates, and to process the results
     */
    @Test
    @DisplayName("Send queries and update statements to the data source")
    public void sendQueriesAndUpdateToTheDataSource(Connection conn) throws SQLException {
        Statement stm = conn.createStatement();
        stm.executeUpdate("CREATE TABLE TABLE_C1 (ID INT)");
        stm.executeUpdate("INSERT INTO TABLE_C1(ID) VALUES(1)");
        ResultSet res = stm.executeQuery("SELECT TABLE_C1.ID FROM TABLE_C1");
        res.next();
        Assertions.assertEquals(1,res.getInt(1));
    }
}
