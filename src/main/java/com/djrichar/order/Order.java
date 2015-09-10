package com.djrichar.order;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity represents an Order.
 * the order will have a genereated Id
 * the order will have a header.  the system may modify the header to identify the source
 * and Order contains N number of OrderLines.
 */
@Entity
@Table(name = "tOrder")
public class Order implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "header")
    private String header;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderLine> lines = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Order() {/*JPA CONSTRUCTOR*/}

    /**
     * get the lines for this Order
     * the List is not modifiable you must add new Lines to the order.
     *
     * @return
     */
    public List<OrderLine> getLines() {
        return lines;
    }

    public void addLine(OrderLine line) {
        this.lines.add(line);
        if (line.getOrder() != this) {
            line.setOrder(this);
        }
    }

    public boolean isValid() {
        for (OrderLine line : lines) {
            int quantity = line.getQuantity();
            if (quantity < 1 || quantity > 5) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Order{" +
                "hdr:" + header +
                ", lines:" + getLines() +
                '}';
    }
}
