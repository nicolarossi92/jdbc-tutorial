package it.mauluk92.jdbc.c3;

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
 * This class contains tests to validate rules about
 * Updatable result sets. Updatable result set can be modified
 * without using SQL statements but plain Java code
 */
@ExtendWith({ConnectionParameterResolver.class, SqlCallback.class})
public class UpdatableResultSetTest {

    /**
     * Before you can make updates to a {@link ResultSet} object, you need to create
     * one that is updatable. In order to do this, you supply the {@link ResultSet} constant
     * {@code CONCUR_UPDATABLE} to the {@code createStatement} method. The {@link Statement}
     * object that is created will produce an updatable {@link ResultSet} object each time it executes
     * a query. We can now use JDBC 2.0 methods in the {@link ResultSet} interface to insert a new row,
     * delete one of its existing row, or modify one of its column values.
     * You might note that just specifying that a result set be updatable does not guarantee that the result
     * set you get is updatable. If a driver does not support updatable result sets, it will return one that is read only.
     */
    @Test
    @JdbcSql("c3/updatable_result_set/creating_updatable_set.sql")
    @DisplayName("Creating an Updatable Result Set")
    public void creatingUpdatableResultSet(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = stmt.executeQuery("SELECT ID, NAME FROM TABLE_C3");
        Assertions.assertEquals(ResultSet.CONCUR_UPDATABLE, resultSet.getConcurrency());
    }

    /**
     * An update is the modification of a column value in the current row.
     * Update operations in the JDBC 2.0 API affect column values in the row where the cursor is positioned.
     * {@link ResultSet} updater methods generally takes two parameters: the column to update and the new
     * value to put in that column. As with the {@link ResultSet} getter methods, the parameter designating the column may be
     * either the column name or the column number. There is a different update method for updating each
     * data type ( {@code updateString}, {@code updateBigDecimal}, {@code updateInt} and so on) just as there
     * are different getter methods for retrieving different data types.
     * To make the update take effect in the DB, we must call the {@link ResultSet} method {@code updateRow}.
     * Note that you must call the method before moving the cursor. If you move the cursor to another row before
     * calling {@code updateRow}, the updates are lost, that is, the row will revert to its previous column values.
     * We can also rollback the updates with {@code cancelRowUpdates}. Once you have called {@code updateRow},
     * the method {@code cancelRowUpdates} won't work. Changes always affect the current row.
     * When a row is inserted, for example, there is no way to know where in the table it has been inserted.
     */
    @Test
    @JdbcSql("c3/updatable_result_set/updating_programmatically.sql")
    @DisplayName("Updating a Result Set Programmatically")
    public void updatingProgrammatically(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = stmt.executeQuery("SELECT ID, NAME FROM TABLE_C3");
        resultSet.next(); // Getting the first row
        resultSet.updateString("NAME", "UPDATED!"); // Updating the first row name!
        resultSet.updateRow(); // Confirming updates!
        Assertions.assertEquals("UPDATED!", resultSet.getString("NAME"));

        resultSet.next(); // Getting second row
        resultSet.updateString("NAME", "UPDATED!"); // Updating second row
        resultSet.cancelRowUpdates(); // Cancelling update
        Assertions.assertNotEquals("UPDATED!", resultSet.getString("NAME"));
    }

    /**
     * You can also insert a new row into a table or delete an existing row programmatically
     * withe JDBC API, without having to resort to SQL statements.
     * The first step is to move the cursor to the insert row, which you do by invoking
     * the method {@code moveToInsertRow}. The next step is to set a value for each column in the row.
     * You do this by calling the appropriate updater method for each value.
     * Note that these are the same updater methods you used in the previous test for changing a column value.
     * Finally, you call the method {@code insertRow} to insert the row you have just populated with values into the result
     * set. This one method simultaneously inserts the row into both the {@link ResultSet} object
     * and the database table from which the result set was selected.
     * What happens when you do not supply every value? A {@code NULL} value is used here.
     * After you have called the method {@code insertRow}, you can start building another row to be inserted,
     * or you can move the cursor back to a result set row.
     * Note that you can move the cursor back to a result set row. If you move outside insert row without
     * first calling {@code insertRow} method, all data will be lost. You can use also method {@code moveToCurrentRow}
     * to move back to the previous current row, before moving out of the insert row.
     */
    @Test
    @JdbcSql("c3/updatable_result_set/inserting_programmatically.sql")
    @DisplayName("Inserting a Row into a Result Set Programmatically")
    public void insertingRowProgrammatically(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = stmt.executeQuery("SELECT ID, NAME FROM TABLE_C3");
        resultSet.last(); // Go to the last row
        Assertions.assertEquals(3, resultSet.getRow()); // Count number of rows
        resultSet.beforeFirst(); // Reset the row
        resultSet.moveToInsertRow(); // Moving to insertRow

        // Creating a new row to insert

        resultSet.updateInt("ID", 4);
        resultSet.updateString("NAME", "INSERTED_ROW");

        // Inserting a new row programmatically

        resultSet.insertRow();

        // Getting new result set

        ResultSet resultSetNew = stmt.executeQuery("SELECT ID, NAME FROM TABLE_C3");

        resultSetNew.last();

        // Verifying that result set is now bigger

        Assertions.assertEquals(4, resultSetNew.getRow());
    }

    /**
     * So far, you have seen how to update a column value and how to insert a new row.
     * Deleting a row is the third way to modify a {@link ResultSet} object, and it is the simplest.
     * You simply move the cursor to the row you want to delete and then call the method
     * {@code deleteRow}.
     * The only issue about deletions is what the {@link ResultSet} object actually does when it deletes
     * a row. With some JDBC drivers, a deleted row is removed and is no longer visible in a result set.
     * Some JDBC drivers use a blank row in place of the deleted row.
     */
    @Test
    @JdbcSql("c3/updatable_result_set/deleting_programmatically.sql")
    @DisplayName("Deleting a Row Programmatically")
    public void deletingARowProgrammatically(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = stmt.executeQuery("SELECT ID, NAME FROM TABLE_C3");
        resultSet.next();
        resultSet.deleteRow(); // Deleting the first row

        ResultSet resultSetNew = stmt.executeQuery("SELECT ID, NAME FROM TABLE_C3");

        resultSetNew.last(); // Go to last row

        Assertions.assertEquals(2, resultSetNew.getRow());
    }




}
