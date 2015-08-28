package com.djrichar.runner;

import com.djrichar.DataStore;
import com.djrichar.inventory.InventoryItem;
import com.djrichar.inventory.InventoryManager;
import com.djrichar.order.Order;
import com.djrichar.order.OrderLine;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by daniel on 8/27/15.
 */
public class OrderGenerator implements Callable<String>{

    private static int numberOfThreads = 3;
    private List<String> itemNames;
    private long header = 1l;

    public OrderGenerator(){
        this("A", "B", "C", "D", "E");
    }

    public OrderGenerator(String... itemNames){
        this.itemNames = Collections.unmodifiableList(Arrays.asList(itemNames));
    }

    /**
     * helper method to randomly generate and order
     * @return
     */
    private Order createOrder(){
        Order order = new Order();
        order.setHeader(header++);
        Random rand = new Random();
        int numberOfLineItems = rand.nextInt(10);
        for(int i=0;i<numberOfLineItems;i++){
            order.addLine(new OrderLine(itemNames.get(rand.nextInt(itemNames.size())), rand.nextInt(6)));
        }
        return order;
    }

    @Override
    public String call() throws Exception {
        StringBuilder runnerResults = new StringBuilder();
        InventoryManager mgr = InventoryManager.getInstance();
        while(mgr.hasInventory()) {
            runnerResults.append(
                    mgr.processOrder(createOrder())
            );
        }
        return runnerResults.toString();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {
        DataStore.initialize(null);
        DataStore ds = new DataStore();

        ds.insertInventoryItem(new InventoryItem("A", 150, 0));
        ds.insertInventoryItem(new InventoryItem("B", 150, 0));
        ds.insertInventoryItem(new InventoryItem("C", 100, 0));
        ds.insertInventoryItem(new InventoryItem("D", 100, 0));
        ds.insertInventoryItem(new InventoryItem("E", 200, 0));

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        service.invokeAll(Arrays.asList(new OrderGenerator("A", "B", "C"), new OrderGenerator("C", "D", "E"), new OrderGenerator("A", "C", "D", "D")));
        service.shutdown();
        while(!service.isTerminated()){
            Thread.sleep(1000);
        }

        System.out.println(InventoryManager.getInstance().getResults());
    }
}
