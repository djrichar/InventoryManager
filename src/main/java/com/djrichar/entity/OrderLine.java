package com.djrichar.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * the Order line contains a mapping to the entity, inventory and fulfillment
 * there is a generated Id
 * there are many OrderLines to a single Order
 * there are mand OrderLines to a single Inventory
 * there is a qantity that repersents the number of items wanted from the Inventory
 * there is a one to one mapping with the fulfillment object
 */
@Entity
@Table(name = "orderLine")
public class OrderLine implements Serializable {


    @Id
    @GeneratedValue
    long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "name")
    private InventoryItem item;

    @Column(name = "quantity")
    private int quantity;

    @OneToOne(cascade = CascadeType.ALL, targetEntity = Fulfillment.class, mappedBy = "line")
    private Fulfillment fulfillment;

    OrderLine() {/*JPA CONSTRUCTOR*/}

    public OrderLine(String itemName, int quantity) {
        this(new InventoryItem(itemName, 0, 0), quantity);
    }

    public OrderLine(InventoryItem item, int quantity) {
        this.quantity = quantity;
        this.item = item;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public InventoryItem getItem() {
        return item;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public Fulfillment getFulfillment() {
        if (fulfillment == null) {
            fulfillment = new Fulfillment(this);
        }
        return fulfillment;
    }

    @Override
    public String toString() {
        return (item == null ? "??" : item.getName()) + ":" + fulfillment;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderLine)) return false;

        OrderLine line = (OrderLine) o;

        if (id != line.id) return false;
        if (getQuantity() != line.getQuantity()) return false;
        if (!getOrder().getHeader().equals(line.getOrder().getHeader())) return false;
        return !(getItem() != null ?
                !(line.getItem() != null && getItem().getName().equals(line.getItem().getName())) : line.getItem() != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + getOrder().getHeader().hashCode();
        result = 31 * result + (getItem() != null ? getItem().getName().hashCode() : 0);
        result = 31 * result + getQuantity();
        return result;
    }
}
