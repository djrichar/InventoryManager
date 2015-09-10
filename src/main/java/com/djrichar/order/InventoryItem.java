package com.djrichar.order;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * InventoryItem repersents the number of items in stock.
 * it has a unique name which is also the key
 * it manages the current stock
 * it manages total backorders
 */
@Entity
@Table(name = "Item")
public class InventoryItem implements Serializable {

    @Id
    @Column(name = "name", unique = true, nullable = false, length = 128)
    private String name;

    @Column(name = "inStock")
    private long inStock;

    @Column(name = "backOrdered")
    private long backOrdered;

    InventoryItem() {/*JPA CONSTRUCTOR*/}

    public InventoryItem(String itemName) {
        this(itemName, 0, 0);
    }

    public InventoryItem(String itemName, long instock, long backordered) {
        this.name = itemName;
        this.inStock = instock;
        this.backOrdered = backordered;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getInStock() {
        return inStock;
    }

    /**
     * decrements the inStock value by the numberToShip
     *
     * @param numberToShip
     */
    public void ship(long numberToShip) {
        inStock -= numberToShip;
        if (inStock < 0) {
            inStock += numberToShip;
            throw new IllegalArgumentException("Inventory cannot fall below 0");
        }
    }

    public long getBackOrdered() {
        return backOrdered;
    }

    /**
     * increments the number to backorder
     *
     * @param numberToBackorder
     */
    public void backorder(long numberToBackorder) {
        backOrdered += numberToBackorder;
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "name='" + name + '\'' +
                ", instock=" + inStock +
                ", backordered=" + backOrdered +
                '}';
    }
}
