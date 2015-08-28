package com.djrichar.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by daniel on 8/27/15.
 */
public class Order {

    Long header;
    List<OrderLine> lines = new ArrayList<>();

    public Long getHeader() {
        return header;
    }

    public void setHeader(Long header) {
        this.header = header;
    }

    /**
     * get the lines for this Order
     * the List is not modifiable you must add new Lines to the order.
     * @return
     */
    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public void addLine(OrderLine line) {
        this.lines.add(line);
    }

    public boolean isValid(){
        for(OrderLine line : lines){
            int quantity = line.getQuantity();
            if(quantity < 1 || quantity > 5){
                return false;
            }
        }
        return true;
    } }
