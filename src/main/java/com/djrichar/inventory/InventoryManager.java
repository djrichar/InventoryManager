package com.djrichar.inventory;

import com.djrichar.DataStore;
import com.djrichar.DataStoreException;
import com.djrichar.order.Order;
import com.djrichar.order.OrderLine;

import java.util.ArrayList;
import java.util.List;

/**
 * this class is responsible for processing all Orders in the order they are recieved.
 */
public class InventoryManager {
    private static InventoryManager INSTANCE = null;
    public static InventoryManager getInstance() {
        if(INSTANCE==null){
            INSTANCE = new InventoryManager();
        }
        return INSTANCE;
    }

    /**
     * Singleton class
     */
    private InventoryManager(){

    }
    private StringBuilder processedOutput = new StringBuilder();
    private boolean hasInventory = true;

    /**
     * we can only process one order at a time. so therefore this method must be synchronized.
     */
    public synchronized String processOrder(Order order) throws DataStoreException {
        if(!order.isValid()){
            return "";
        }

        long threadId = Thread.currentThread().getId();
        StringBuilder result = new StringBuilder("Header:"+order.getHeader()+"-"+threadId+":[\n\t");

        DataStore ds = new DataStore();
        for(OrderLine line : order.getLines()) {
            InventoryItem item = ds.lookupInventoryItem(line.getItem());
            if (item != null) {
                if (item.getInstock() < line.getQuantity()) {
                    item.backorder(line.getQuantity());
                    line.setStatus(OrderLine.Status.BACKORDERED);
                } else {
                    item.ship(line.getQuantity());
                    line.setStatus(OrderLine.Status.FILLED);
                }
                ds.updateInventoryItem(item);
                ds.insertOrderLine(line);
            }
            result.append(line.stat()).append(String.format("{%d-%d},\n\t",item.getInstock(), item.getBackordered()));
        }
        hasInventory = ds.calculateTotalInventory() > 0;
        String output = result.replace(result.length()-3,result.length(),"\n]\n")
                .toString();
        processedOutput.append(output);
        return output;
    }

    public boolean hasInventory(){
        return hasInventory;
    }

    public String getResults(){
        return processedOutput.toString();
    }

    /**
     * Test method helper to reset results across
     */
    protected static void clearResults(){
        getInstance().hasInventory = true;
        getInstance().processedOutput = new StringBuilder();
    }
}
