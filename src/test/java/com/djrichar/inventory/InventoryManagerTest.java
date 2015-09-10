package com.djrichar.inventory;

import com.djrichar.DataStore;
import com.djrichar.order.Fulfillment;
import com.djrichar.order.InventoryItem;
import com.djrichar.order.Order;
import com.djrichar.order.OrderLine;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        DataStore.initialize();
    }

    @Before
    public void updateDB(){
        try (Session session= DataStore.getSessionFactory().openSession()) {
            session.beginTransaction();
            for(Object order : session.createCriteria(Order.class).list()){
                session.delete(order);
            }
            for(InventoryItem item : createItems()) {
                session.merge(item);
            }
            session.getTransaction().commit();
        }
    }
    private static List<InventoryItem> createItems(){
        List<InventoryItem> items = new ArrayList<>();

        items.add(new InventoryItem("A", 10, 0));
        items.add(new InventoryItem("B", 11, 0));
        items.add(new InventoryItem("C", 12, 0));
        items.add(new InventoryItem("D", 13, 0));
        items.add(new InventoryItem("E", 14, 0));
        items.add(new InventoryItem("F", 3, 0));

        return items;
    }

    @Test
    public void processOneOrder() {
        Map<OrderLine,String> lineToExpectedResult = new HashMap<>();

        Order order = new Order();
        order.setHeader("1");
        Order expectedOrder = new Order();
        expectedOrder.setHeader("1-"+Thread.currentThread().getId());
        fillLineForOrder(order, expectedOrder, "A", 1, Fulfillment.Status.FILLED, 9, 0);
        fillLineForOrder(order, expectedOrder, "A", 2, Fulfillment.Status.FILLED, 7, 0);

        String result = InventoryManager.getInstance().processOrder(order);
        String expected = expectedOrder.toString();
        Assert.assertEquals(expected, result);
        Assert.assertEquals(expected + "\n", InventoryManager.getInstance().getResults());
        Assert.assertTrue(InventoryManager.getInstance().hasInventory());
    }

    @Test
    public void proccessAllDataOfOneItem() {
        Map<OrderLine,String> lineToExpectedResult = new HashMap<>();

        Order order = new Order();
        order.setHeader("1");
        Order expectedOrder = new Order();
        expectedOrder.setHeader("1-"+Thread.currentThread().getId());
        fillLineForOrder(order, expectedOrder, "A", 1, Fulfillment.Status.FILLED, 9, 0);
        fillLineForOrder(order, expectedOrder, "A", 4, Fulfillment.Status.FILLED, 5, 0);

        String result = InventoryManager.getInstance().processOrder(order);
        String expected = expectedOrder.toString();

        Order order2 = new Order();
        order2.setHeader("2");
        Order expectedOrder2 = new Order();
        expectedOrder2.setHeader("2-"+Thread.currentThread().getId());
        fillLineForOrder(order2, expectedOrder2, "A", 1, Fulfillment.Status.FILLED, 4, 0);
        fillLineForOrder(order2, expectedOrder2, "A", 4, Fulfillment.Status.FILLED, 0, 0);

        String result2 = InventoryManager.getInstance().processOrder(order2);
        String expected2 = expectedOrder2.toString();

        Order order3 = new Order();
        order3.setHeader("3");
        Order expectedOrder3 = new Order();
        expectedOrder3.setHeader("3-"+Thread.currentThread().getId());
        fillLineForOrder(order3, expectedOrder3, "A", 1, Fulfillment.Status.BACKORDERED, 0, 1);

        String result3 = InventoryManager.getInstance().processOrder(order3);
        String expected3 = expectedOrder3.toString();

        String finalResult = expected + "\n" + expected2 + "\n" + expected3 + "\n";

        Assert.assertEquals(expected, result);
        Assert.assertEquals(expected2, result2);
        Assert.assertEquals(expected3, result3);
        Assert.assertEquals(finalResult, InventoryManager.getInstance().getResults());
        Assert.assertTrue(InventoryManager.getInstance().hasInventory());
    }

    private void fillLineForOrder(Order order, Order expectedOrder, String name, Integer quantity, Fulfillment.Status status, Integer stillInStock,Integer backordered){
        OrderLine line = new OrderLine(new InventoryItem(name, 0, 0),quantity);
        order.addLine(line);

        OrderLine expectedLine = new OrderLine(new InventoryItem(name, 0, 0),quantity);
        expectedLine.getFulfillment().setStatus(status);
        expectedLine.getFulfillment().setInStock(stillInStock);
        expectedLine.getFulfillment().setBackOrdered(backordered);
        expectedOrder.addLine(expectedLine);
    }
    
    @Test
    public void proccessAllDataOfAllItems() {
        Map<OrderLine,String> lineToExpectedResult = new HashMap<>();
        
        Order order = new Order();
        order.setHeader("1");
        Order expectedOrder = new Order();
        expectedOrder.setHeader("1-"+Thread.currentThread().getId());
        fillLineForOrder(order, expectedOrder, "A", 5, Fulfillment.Status.FILLED, 5, 0);
        fillLineForOrder(order, expectedOrder, "A", 5, Fulfillment.Status.FILLED, 0, 0);
        fillLineForOrder(order, expectedOrder, "B", 5, Fulfillment.Status.FILLED, 6, 0);
        fillLineForOrder(order, expectedOrder, "B", 5, Fulfillment.Status.FILLED, 1, 0);
        fillLineForOrder(order, expectedOrder, "B", 1, Fulfillment.Status.FILLED, 0, 0);
        fillLineForOrder(order, expectedOrder, "C", 5, Fulfillment.Status.FILLED, 7, 0);
        fillLineForOrder(order, expectedOrder, "C", 5, Fulfillment.Status.FILLED, 2, 0);
        fillLineForOrder(order, expectedOrder, "C", 2, Fulfillment.Status.FILLED, 0, 0);
        fillLineForOrder(order, expectedOrder, "D", 5, Fulfillment.Status.FILLED, 8, 0);
        fillLineForOrder(order, expectedOrder, "D", 5, Fulfillment.Status.FILLED, 3, 0);
        fillLineForOrder(order, expectedOrder, "D", 3, Fulfillment.Status.FILLED, 0, 0);
        fillLineForOrder(order, expectedOrder, "E", 5, Fulfillment.Status.FILLED, 9, 0);
        fillLineForOrder(order, expectedOrder, "E", 5, Fulfillment.Status.FILLED, 4, 0);
        fillLineForOrder(order, expectedOrder, "E", 4, Fulfillment.Status.FILLED, 0, 0);
        fillLineForOrder(order, expectedOrder, "F", 3, Fulfillment.Status.FILLED, 0, 0);

        String result = InventoryManager.getInstance().processOrder(order);
        String expected = expectedOrder.toString();
        Assert.assertEquals(expected, result);

        Assert.assertFalse("Inventory should be 0", InventoryManager.getInstance().hasInventory());

        Order order2 = new Order();
        order2.setHeader("2");
        Order expectedOrder2 = new Order();
        expectedOrder2.setHeader("2-"+Thread.currentThread().getId());
        fillLineForOrder(order, expectedOrder, "A", 1, Fulfillment.Status.BACKORDERED, 0, 1);
        fillLineForOrder(order, expectedOrder, "B", 4, Fulfillment.Status.BACKORDERED, 0, 4);

        String result2 = InventoryManager.getInstance().processOrder(order2);
        String expected2 = expectedOrder2.toString();
        String finalResult = expected + "\n" + expected2 + "\n";
        Assert.assertEquals(expected2, result2);
        Assert.assertEquals(finalResult, InventoryManager.getInstance().getResults());
        Assert.assertFalse(InventoryManager.getInstance().hasInventory());
    }

}
