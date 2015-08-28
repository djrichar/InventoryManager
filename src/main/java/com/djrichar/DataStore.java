package com.djrichar;

import com.djrichar.inventory.InventoryItem;
import com.djrichar.order.OrderLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * TODO move Objects to JPA
 */
public class DataStore {
    private static final Logger log = LoggerFactory.getLogger(DataStore.class);
    private static String jdbcConnectionString = "jdbc:h2:mem:runner;DB_CLOSE_DELAY=-1";

    /**
     * this is a simple initialization of the h2 db. with 2 tables InventoryItem and OrderLine
     * @param jdbcConnectionString
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void initialize(String jdbcConnectionString) throws ClassNotFoundException, SQLException {
        if(jdbcConnectionString!=null){
            DataStore.jdbcConnectionString = jdbcConnectionString;
        }
        Class.forName("org.h2.Driver");
        try(Connection conn = getConnection()){
            Statement statement = conn.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS InventoryItem(" +
                    "name VARCHAR(32) PRIMARY KEY, " +
                    "stock INT DEFAULT 0, " +
                    "backordered INT DEFAULT 0)");
            statement.execute("CREATE TABLE IF NOT EXISTS OrderLine(" +
                    "id IDENTITY NOT NULL IDENTITY, " +
                    "itemName VARCHAR(32) NOT NULL, " +
                    "status VARCHAR(15) NOT NULL, " +
                    "quantity INT DEFAULT 0," +
                    "FOREIGN KEY (itemName) REFERENCES InventoryItem(name))");
            statement.close();
        }
    }

    private static final Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcConnectionString, "sa", "");
    }


    private InventoryItem buildInventory(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        long instock = rs.getLong("stock");
        long backordered = rs.getLong("backordered");
        return new InventoryItem(name, instock, backordered);
    }
    /**
     * query the DB for the Inventory Item with the given Name
     * @param itemName
     * @return  Item found or null if not found
     * @throws DataStoreException     an error against the DataStore
     */
    public InventoryItem lookupInventoryItem(String itemName) throws DataStoreException {
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM InventoryItem WHERE name = ?")
        ) {
            statement.setString(1, itemName);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                return buildInventory(rs);
            }
        } catch (SQLException e) {
            log.error(String.format("An Error Occurred when looking of InventoryItem %s", itemName), e);
        }
        return null;
    }

    /**
     * get the number of Items still in stock for all items
     * @return
     */
    public int calculateTotalInventory(){
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT SUM(stock) FROM InventoryItem")
        ) {
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error(String.format("An Error Occurred when calculating total Inventory"), e);
        }
        return 0;
    }

    /**
     * create a new Inventory Item
     * @param item
     */
    public void insertInventoryItem(InventoryItem item) {
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO InventoryItem VALUES(?,?,?)")
        ) {
            statement.setString(1, item.getItemName());
            statement.setLong(2, item.getInstock());
            statement.setLong(3, item.getBackordered());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("An Error Occurred when inserting InventoryItem %s", item), e);
        }

    }

    /**
     * update and existing inventory item
     * @param item
     */
    public void updateInventoryItem(InventoryItem item) {
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE InventoryItem SET stock=?, backordered=? WHERE name = ?")
        ) {
            statement.setLong(1, item.getInstock());
            statement.setLong(2, item.getBackordered());
            statement.setString(3, item.getItemName());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("An Error Occurred when updating InventoryItem %s", item), e);
        }
    }

    /**
     * create a new OrderLine
     * @param line
     */
    public void insertOrderLine(OrderLine line) {
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO OrderLine VALUES(null,?,?,?)")
        ) {
            statement.setString(1, line.getItem());
            statement.setString(2, line.getStatus().name());
            statement.setLong(3, line.getQuantity());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("An Error Occurred when inserting InventoryItem %s", line), e);
        }

    }

}
