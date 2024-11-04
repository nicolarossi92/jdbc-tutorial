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

/**
 * This class contains tests to validate rules concerning the retrieval of data from result sets
 */
@ExtendWith({ConnectionParameterResolver.class, SqlCallback.class})
public class RetrievingFromResultSetsTest {
    /**
     * The method next moves what is called a cursor to the next row and makes that
     * row (called the current row) the one upon which we can operate. Since the cursor
     * is initially positioned just above the first row and makes it the current row.
     * Successive invocations of the method next moves the cursor forward one row at a time from the first
     * row to the last row. It returns true as long the cursor in on a valid row. When the cursor
     * goes beyond the last row, the method returns false
     */
    @Test
    @DisplayName("Using the method next")
    @JdbcSql("c2/retrieving_from_result_sets/using_the_method_next.sql")
    public void usingTheMethodNext(Connection conn) throws SQLException {
        ResultSet res = conn.createStatement().executeQuery("SELECT * FROM TABLE_C2");
        res.next(); // this first invocation positions the cursor on the first row
        boolean condition;
        while(condition = res.next()){
            // subsequent invocations move the cursor forward till there is a valid row
        }
        Assertions.assertFalse(condition); // eventually, the last row is reached and false is returned
    }

    /**
     * We use a getter method of the appropriate type to retrieve the value in each column once
     * we have the current row positioned
     */
    @Test
    @DisplayName("Using the method next")
    @JdbcSql("c2/retrieving_from_result_sets/retrieving_column_values.sql")
    public void retrievingColumnValues(Connection conn) throws SQLException {
        ResultSet res = conn.createStatement().executeQuery("SELECT ID, NAME FROM TABLE_C2");
        res.next(); // We are now in the position to retrieve the value in each column in this row
        res.getInt("ID"); // we can use the column label to retrieve the value
        res.getInt(1); // or we can use the column index
        Assertions.assertNotNull(res.getString("NAME")); // we can use the proper type associated with the column
    }
}
