package com.djrichar.inventory;

import com.djrichar.DataStore;
import com.djrichar.entity.InventoryItem;
import com.djrichar.entity.Order;
import com.djrichar.runner.OrderGenerator;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 8/28/15.
 */
public class OrderGeneratorTest {

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
                session.save(item);
            }
            session.getTransaction().commit();
        }
    }

    private static List<InventoryItem> createItems(){
        List<InventoryItem> items = new ArrayList<>();
        items.add(new InventoryItem("A",3, 0));
        items.add(new InventoryItem("B", 3, 0));

        return items;
    }

    @Test
    public void testOrderGeneration() throws Exception {

        String result = new OrderGenerator("A", "B").call();
        Assert.assertEquals(result, InventoryManager.getInstance().getResults());
    }
}
