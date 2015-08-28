package com.djrichar.inventory;

import com.djrichar.DataStore;
import com.djrichar.runner.OrderGenerator;
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
        DataStore.initialize("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1");
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
        items.add(new InventoryItem("A",3, 0));
        items.add(new InventoryItem("B", 3, 0));

        return items;
    }

    @Test
    public void testOrderGeneration() throws Exception {
        String result = new OrderGenerator("A","B").call();
        Assert.assertEquals(result, InventoryManager.getInstance().getResults());
    }
}
