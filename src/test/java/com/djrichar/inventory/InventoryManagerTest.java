package com.djrichar.inventory;

import com.djrichar.DataStore;
import com.djrichar.DataStoreException;
import com.djrichar.inventory.InventoryItem;
import com.djrichar.inventory.InventoryManager;
import com.djrichar.order.Order;
import com.djrichar.order.OrderLine;
import com.djrichar.runner.OrderGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 8/27/15.
 */
public class InventoryManagerTest {

    @BeforeClass
    public static void setup() throws SQLException, ClassNotFoundException {
        DataStore.initialize("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        DataStore ds = new DataStore();
        for(InventoryItem ii : createItems()){
            ds.insertInventoryItem(ii);
        }
    }

    @Before
    public void updateDB(){
        InventoryManager.clearResults();
        DataStore ds = new DataStore();
        for(InventoryItem ii : createItems()){
            ds.updateInventoryItem(ii);
        }
    }
    private static List<InventoryItem> createItems(){
        List<InventoryItem> items = new ArrayList<>();
        items.add(new InventoryItem("A",10, 0));
        items.add(new InventoryItem("B",11, 0));
        items.add(new InventoryItem("C",12, 0));
        items.add(new InventoryItem("D",13, 0));
        items.add(new InventoryItem("E",14, 0));
        items.add(new InventoryItem("F", 3, 0));
        return items;
    }

    private String generateExpectedStatString(Order order, Map<OrderLine,String> lineToExpectedResult) throws DataStoreException {
        StringBuilder st = new StringBuilder(
                String.format("Header:%d-%d:[\n\t", order.getHeader(), Thread.currentThread().getId()));
        for(OrderLine line : order.getLines()){
            String[] stockValue = lineToExpectedResult.get(line).split(":");
            st.append(line.stat()).append(String.format("{%s-%s},\n\t", stockValue[0], stockValue[1]));
        }
        int index = st.lastIndexOf(",\n\t");
        return st.replace(index,index+3,"\n]\n").toString();
    }
    @Test
    public void processOneOrder() throws DataStoreException {
        Map<OrderLine,String> lineToExpectedResult = new HashMap<>();

        Order order = new Order();
        order.setHeader(1l);
        fillMapAndOrder(lineToExpectedResult, order, "A", 1, "9:0");
        fillMapAndOrder(lineToExpectedResult, order, "B", 4, "7:0");

        String result = InventoryManager.getInstance().processOrder(order);

        //mark orderlines as success
        order.getLines().get(0).setStatus(OrderLine.Status.FILLED);
        order.getLines().get(1).setStatus(OrderLine.Status.FILLED);

        String expected = generateExpectedStatString(order,lineToExpectedResult);
        Assert.assertEquals(expected, result);
        Assert.assertEquals(InventoryManager.getInstance().getResults(), result);
        Assert.assertTrue(InventoryManager.getInstance().hasInventory());
    }

    @Test
    public void proccessAllDataOfOneItem() throws DataStoreException {
        Map<OrderLine,String> lineToExpectedResult = new HashMap<>();

        Order order = new Order();
        order.setHeader(1l);
        fillMapAndOrder(lineToExpectedResult, order, "A", 1, "9:0");
        fillMapAndOrder(lineToExpectedResult, order, "A", 4, "5:0");

        String result = InventoryManager.getInstance().processOrder(order);
        String expected = generateExpectedStatString(order, lineToExpectedResult);

        Order order2 = new Order();
        order2.setHeader(2l);
        fillMapAndOrder(lineToExpectedResult, order2, "A", 1, "4:0");
        fillMapAndOrder(lineToExpectedResult, order2, "A", 4, "0:0");

        String result2 = InventoryManager.getInstance().processOrder(order2);
        String expected2 = generateExpectedStatString(order2, lineToExpectedResult);

        Order order3 = new Order();
        order3.setHeader(3l);
        fillMapAndOrder(lineToExpectedResult, order3, "A", 1, "0:1");

        String result3 = InventoryManager.getInstance().processOrder(order3);
        String expected3 = generateExpectedStatString(order3, lineToExpectedResult);

        order.getLines().get(0).setStatus(OrderLine.Status.FILLED);
        order.getLines().get(1).setStatus(OrderLine.Status.FILLED);

        order2.getLines().get(0).setStatus(OrderLine.Status.FILLED);
        order2.getLines().get(1).setStatus(OrderLine.Status.FILLED);

        order3.getLines().get(0).setStatus(OrderLine.Status.BACKORDERED);


        String finalResult = expected + expected2 + expected3;

        Assert.assertEquals(expected, result);
        Assert.assertEquals(expected2, result2);
        Assert.assertEquals(expected3, result3);
        Assert.assertEquals(finalResult, InventoryManager.getInstance().getResults());
        Assert.assertTrue(InventoryManager.getInstance().hasInventory());
    }

    private void fillMapAndOrder(Map<OrderLine, String> expectedResults, Order order, String name, Integer quantity, String expectedTotals){
        OrderLine line = new OrderLine(name, quantity);
        expectedResults.put(line, expectedTotals);
        order.addLine(line);
    }
    @Test
    public void proccessAllDataOfAllItems() throws DataStoreException {
        Map<OrderLine,String> lineToExpectedResult = new HashMap<>();
        
        
        Order order = new Order();
        order.setHeader(1l);
        fillMapAndOrder(lineToExpectedResult, order, "A", 5, "5:0");
        fillMapAndOrder(lineToExpectedResult, order, "A", 5, "0:0");
        fillMapAndOrder(lineToExpectedResult, order, "B", 5, "6:0");
        fillMapAndOrder(lineToExpectedResult, order, "B", 5, "1:0");
        fillMapAndOrder(lineToExpectedResult, order, "B", 1, "0:0");
        fillMapAndOrder(lineToExpectedResult, order, "C", 5, "7:0");
        fillMapAndOrder(lineToExpectedResult, order, "C", 5, "2:0");
        fillMapAndOrder(lineToExpectedResult, order, "C", 2, "0:0");
        fillMapAndOrder(lineToExpectedResult, order, "D", 5, "8:0");
        fillMapAndOrder(lineToExpectedResult, order, "D", 5, "3:0");
        fillMapAndOrder(lineToExpectedResult, order, "D", 3, "0:0");
        fillMapAndOrder(lineToExpectedResult, order, "E", 5, "9:0");
        fillMapAndOrder(lineToExpectedResult, order, "E", 5, "4:0");
        fillMapAndOrder(lineToExpectedResult, order, "E", 4, "0:0");
        fillMapAndOrder(lineToExpectedResult, order, "F", 3, "0:0");

        String result = InventoryManager.getInstance().processOrder(order);
        String expected = generateExpectedStatString(order, lineToExpectedResult);
        Assert.assertEquals(expected, result);

        Assert.assertFalse("Inventory should be 0", InventoryManager.getInstance().hasInventory());

        Order order2 = new Order();
        order2.setHeader(2l);
        fillMapAndOrder(lineToExpectedResult, order2, "A", 1, "0:1");
        fillMapAndOrder(lineToExpectedResult, order2, "B", 4, "0:4");

        String result2 = InventoryManager.getInstance().processOrder(order2);
        String expected2 = generateExpectedStatString(order2, lineToExpectedResult);
        String finalResult = expected + expected2;
        Assert.assertEquals(expected2, result2);
        Assert.assertEquals(finalResult, InventoryManager.getInstance().getResults());
        Assert.assertFalse(InventoryManager.getInstance().hasInventory());
    }

}
