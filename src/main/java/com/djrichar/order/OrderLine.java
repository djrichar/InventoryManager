package com.djrichar.order;

import com.djrichar.inventory.InventoryItem;

/**
 * Created by daniel on 8/27/15.
 */
public class OrderLine {

    public enum Status{
        NOT_FILLED,
        BACKORDERED,
        FILLED
    }

    private String item;
    private int quantity;
    private Status status;
    
    public OrderLine(String item, int quantity) {
        this.quantity = quantity;
        this.item = item;
        this.status = Status.NOT_FILLED;
    }

    public String getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String stat() {
        int ordered = status.equals(Status.FILLED) ? quantity : 0;
        int backOrdered = status.equals(Status.BACKORDERED) ? quantity : 0;

        return String.format("%s:[%d,%d,%d]", item, quantity, ordered, backOrdered);
    }
}
