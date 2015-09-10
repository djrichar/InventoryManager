package com.djrichar.order;

import javax.persistence.*;
import java.io.Serializable;

/**
 * fulfillment is the data stored after an orderLine has been processed.
 * the fulfillment contains an id the is the foriegnKey to the OrderLine the fulfillment is for.
 * the status is if the orderLine has been processed, filled, or backordered
 * keeps a record of the stock after fulfillment is done
 * keeps a record of backorders after fulfillment is done
 */
@Entity
@Table(name = "fullfillment")
public class Fulfillment implements Serializable {
    public enum Status {
        NOT_FILLED,
        BACKORDERED,
        FILLED
    }

    @Id
    @OneToOne(targetEntity = OrderLine.class)
    @JoinColumn(name = "id")
    private OrderLine line;

    @Column(name = "inStock")
    private long inStock;

    @Column(name = "backOrdered")
    private long backOrdered;

    @Enumerated(EnumType.STRING)
    private Status status;

    Fulfillment() {/*JPA CONSTRUCTOR*/}

    public Fulfillment(OrderLine line) {
        this.line = line;
        this.status = Status.NOT_FILLED;
    }

    public long getInStock() {
        return inStock;
    }

    public void setInStock(long inStock) {
        this.inStock = inStock;
    }

    public long getBackOrdered() {
        return backOrdered;
    }

    public void setBackOrdered(long backOrdered) {
        this.backOrdered = backOrdered;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fulfillment)) return false;

        Fulfillment that = (Fulfillment) o;

        if (line != null ? !line.equals(that.line) : that.line != null) return false;
        return getStatus() == that.getStatus();

    }

    @Override
    public int hashCode() {
        int result = line != null ? line.hashCode() : 0;
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        return result;
    }

    public String toString() {
        return "{ q:" + line.getQuantity() +
                ", b:" + (status.equals(Status.BACKORDERED) ? line.getQuantity() : "0") +
                ", o:" + (status.equals(Status.FILLED) ? line.getQuantity() : "0") +
                ", stock:" + inStock +
                ", backordered:" + backOrdered + "}";
    }
}
