package com.djrichar.inventory;

/**
 * Created by daniel on 8/27/15.
 */
public class InventoryItem {
    private String itemName;
    private long instock;
    private long backordered;

    public InventoryItem(String itemName, long instock, long backordered) {
        this.itemName = itemName;
        this.instock = instock;
        this.backordered = backordered;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public long getInstock() {
        return instock;
    }

    public long getBackordered() {
        return backordered;
    }

    /**
     * decrements the inStock value by the numberToShip
     * @param numberToShip
     */
    public void ship(long numberToShip) {
        instock -= numberToShip;
        if(instock < 0){
            instock += numberToShip;
            throw new IllegalArgumentException("Inventory cannot fall below 0");
        }
    }

    /**
     * increments the number to backorder
     * @param numberToBackorder
     */
    public void backorder(long numberToBackorder){
        backordered += numberToBackorder;
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "itemName='" + itemName + '\'' +
                ", instock=" + instock +
                ", backordered=" + backordered +
                '}';
    }
}
