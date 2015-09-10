package com.djrichar.runner;

import com.djrichar.DataStore;
import com.djrichar.inventory.InventoryManager;
import com.djrichar.order.InventoryItem;
import com.djrichar.order.Order;
import com.djrichar.order.OrderLine;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by daniel on 8/27/15.
 */
public class OrderGenerator implements Callable<String> {

    private static int numberOfThreads = 3;
    private List<String> itemNames;

    public OrderGenerator(String... itemNames) {
        this.itemNames = Arrays.asList(itemNames);
    }

    /**
     * helper method to randomly generate and order
     *
     * @return
     */
    private Order createOrder(String header) {
        Order order = new Order();
        order.setHeader(header);
        Random rand = new Random();
        int numberOfLineItems = rand.nextInt(10);
        for (int i = 0; i < numberOfLineItems; i++) {
            order.addLine(
                    new OrderLine(
                            new InventoryItem(itemNames.get(rand.nextInt(itemNames.size()))),
                            rand.nextInt(6)));
        }
        return order;
    }

    @Override
    public String call() throws Exception {
        StringBuilder runnerResults = new StringBuilder();
        InventoryManager mgr = InventoryManager.getInstance();
        int header = 1;
        while (mgr.hasInventory()) {
            runnerResults.append(
                    mgr.processOrder(createOrder("" + header++))
            );
        }
        return runnerResults.toString();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {
        DataStore.initialize();
        try {
            try (Session session = DataStore.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.save(new InventoryItem("A", 150, 0));
                session.save(new InventoryItem("B", 150, 0));
                session.save(new InventoryItem("C", 100, 0));
                session.save(new InventoryItem("D", 100, 0));
                session.save(new InventoryItem("E", 200, 0));
                session.getTransaction().commit();
            }

            List<OrderGenerator> orderGenerators = new ArrayList<>();
            orderGenerators.add(new OrderGenerator("A", "B", "C"));
            orderGenerators.add(new OrderGenerator("C", "D", "E"));
            orderGenerators.add(new OrderGenerator("A", "C", "D", "E"));

            ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
            service.invokeAll(orderGenerators);
            service.shutdown();
            while (!service.isTerminated()) {
                Thread.sleep(1000);
            }
            System.out.println(InventoryManager.getInstance().getResults());
        } finally {
            DataStore.destory();
        }
    }
}
