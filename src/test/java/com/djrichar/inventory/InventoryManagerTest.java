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
import java.util.List;

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

    private String generateExpectedStatString(Order order){
        StringBuilder st = new StringBuilder("Header:").append(order.getHeader()).append(":[\n\t");
        for(OrderLine line : order.getLines()){
            st.append(line.stat()).append(",\n\t");
        }
        int index = st.lastIndexOf(",\n\t");
        return st.replace(index,index+3,"\n]\n").toString();
    }
    @Test
    public void processOneOrder() throws DataStoreException {
        Order order = new Order();
        order.setHeader(1l);
        order.addLine(new OrderLine("A", 1));
        order.addLine(new OrderLine("B", 4));

        String result = InventoryManager.getInstance().processOrder(order);

        //mark orderlines as success
        order.getLines().get(0).setStatus(OrderLine.Status.FILLED);
        order.getLines().get(1).setStatus(OrderLine.Status.FILLED);

        String expected = generateExpectedStatString(order);
        Assert.assertEquals(expected, result);
        Assert.assertEquals(InventoryManager.getInstance().getResults(), result);
        Assert.assertTrue(InventoryManager.getInstance().hasInventory());
    }

    @Test
    public void proccessAllDataOfOneItem() throws DataStoreException {
        Order order = new Order();
        order.setHeader(1l);
        order.addLine(new OrderLine("A", 1));
        order.addLine(new OrderLine("A", 4));

        String result = InventoryManager.getInstance().processOrder(order);

        Order order2 = new Order();
        order2.setHeader(2l);
        order2.addLine(new OrderLine("A", 1));
        order2.addLine(new OrderLine("A", 4));

        String result2 = InventoryManager.getInstance().processOrder(order2);

        Order order3 = new Order();
        order3.setHeader(3l);
        order3.addLine(new OrderLine("A", 1));

        String result3 = InventoryManager.getInstance().processOrder(order3);

        order.getLines().get(0).setStatus(OrderLine.Status.FILLED);
        order.getLines().get(1).setStatus(OrderLine.Status.FILLED);

        order2.getLines().get(0).setStatus(OrderLine.Status.FILLED);
        order2.getLines().get(1).setStatus(OrderLine.Status.FILLED);

        order3.getLines().get(0).setStatus(OrderLine.Status.BACKORDERED);

        String expected = generateExpectedStatString(order);
        String expected2 = expected + generateExpectedStatString(order2);
        String finalResult = expected2 + generateExpectedStatString(order3);

        Assert.assertEquals(generateExpectedStatString(order), result);
        Assert.assertEquals(generateExpectedStatString(order2), result2);
        Assert.assertEquals(generateExpectedStatString(order3), result3);
        Assert.assertEquals(finalResult, InventoryManager.getInstance().getResults());
        Assert.assertTrue(InventoryManager.getInstance().hasInventory());
    }

    @Test
    public void proccessAllDataOfAllItems() throws DataStoreException {
        Order order = new Order();
        order.setHeader(1l);
        order.addLine(new OrderLine("A", 5));
        order.addLine(new OrderLine("A", 5));
        order.addLine(new OrderLine("B", 5));
        order.addLine(new OrderLine("B", 5));
        order.addLine(new OrderLine("B", 1));
        order.addLine(new OrderLine("C", 5));
        order.addLine(new OrderLine("C", 5));
        order.addLine(new OrderLine("C", 2));
        order.addLine(new OrderLine("D", 5));
        order.addLine(new OrderLine("D", 5));
        order.addLine(new OrderLine("D", 3));
        order.addLine(new OrderLine("E", 5));
        order.addLine(new OrderLine("E", 5));
        order.addLine(new OrderLine("E", 4));
        order.addLine(new OrderLine("F", 3));

        String result = InventoryManager.getInstance().processOrder(order);
        String expected = generateExpectedStatString(order);
        Assert.assertEquals(expected, result);

        Assert.assertFalse("Inventory should be 0", InventoryManager.getInstance().hasInventory());

        Order order2 = new Order();
        order2.setHeader(2l);
        order2.addLine(new OrderLine("A", 1));
        order2.addLine(new OrderLine("B", 4));

        String result2 = InventoryManager.getInstance().processOrder(order2);
        String expected2 = generateExpectedStatString(order2);
        String finalResult = expected + expected2;
        Assert.assertEquals(expected2, result2);
        Assert.assertEquals(finalResult, InventoryManager.getInstance().getResults());
        Assert.assertFalse(InventoryManager.getInstance().hasInventory());
    }

}
