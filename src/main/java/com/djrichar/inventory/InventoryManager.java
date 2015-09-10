package com.djrichar.inventory;

import com.djrichar.DataStore;
import com.djrichar.entity.Fulfillment;
import com.djrichar.entity.InventoryItem;
import com.djrichar.entity.Order;
import com.djrichar.entity.OrderLine;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.slf4j.LoggerFactory;

/**
 * this class is responsible for processing all Orders in the entity they are relieved.
 */
public class InventoryManager {
    private static InventoryManager INSTANCE = null;

    public static InventoryManager getInstance() {
        if (INSTANCE == null) {
            synchronized (InventoryManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new InventoryManager();
                }
            }
        }
        return INSTANCE;
    }

    private long inventoryTotal = 0;

    /**
     * Singleton class
     */
    private InventoryManager() {
        updateTotalInventory();
    }

    /**
     * aggergate the total instock
     */
    private void updateTotalInventory() {
        try (Session session = DataStore.getSessionFactory().openSession()) {
            inventoryTotal = (long) session.createCriteria(InventoryItem.class).setProjection(Projections.sum("inStock")).uniqueResult();
        }
    }

    /**
     * process one entity at a time.
     * ensure the inventoryItem is updated
     * ensures the fulfillment for an orderLine is updated
     * update the inventoryTotal if an item is shipped.
     */
    public synchronized String processOrder(Order order) {
        if (!order.isValid()) {
            return "";
        }
        boolean updateTotal = false;
        try (Session session = DataStore.getSessionFactory().openSession()) {
            session.beginTransaction();

            for (OrderLine line : order.getLines()) {
                //get the persistent InventoryItem by the key
                InventoryItem item = session.get(InventoryItem.class, line.getItem().getName());
                if (item != null) {
                    Fulfillment fulfillment = line.getFulfillment();
                    if (item.getInStock() < line.getQuantity()) {
                        item.backorder(line.getQuantity());
                        fulfillment.setStatus(Fulfillment.Status.BACKORDERED);
                    } else {
                        item.ship(line.getQuantity());
                        fulfillment.setStatus(Fulfillment.Status.FILLED);
                        updateTotal = true;
                    }
                    fulfillment.setInStock(item.getInStock());
                    fulfillment.setBackOrdered(item.getBackOrdered());
                    session.update(item);
                }
            }
            order.setHeader(String.format("%s-%s", order.getHeader(), Thread.currentThread().getId()));
            session.save(order);
            session.getTransaction().commit();
        } catch (Exception e) {
            LoggerFactory.getLogger(this.getClass()).error("unable to store entity: ", e);
            throw new RuntimeException("Unable to persist Order", e);
        }
        if (updateTotal) {
            updateTotalInventory();
        }
        LoggerFactory.getLogger(this.getClass()).debug("updatedTotal={}, totalInventory={}", updateTotal, inventoryTotal);
        return order.toString();
    }

    /**
     * if an Inventory Item has something in stock
     *
     * @return
     */
    public boolean hasInventory() {
        return inventoryTotal > 0;
    }

    /**
     * prints the orders that have been processed
     *
     * @return
     */
    public String getResults() {
        StringBuilder results = new StringBuilder();
        try (Session session = DataStore.getSessionFactory().openSession()) {
            for (Object order : session.createCriteria(Order.class).list()) {
                results.append(order).append("\n");
            }
        }
        return results.toString();
    }
}
