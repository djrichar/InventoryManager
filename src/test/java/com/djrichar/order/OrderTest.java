package com.djrichar.order;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by daniel on 8/27/15.
 */
public class OrderTest {

    @Test
    public void orderValidTest() {
        Order order = new Order();
        order.setHeader(1l);
        order.addLine(new OrderLine("A", 1));
        order.addLine(new OrderLine("B", 4));

        Assert.assertTrue("order must have all lines with a quantity between 1 and 5 inclusive", order.isValid());
    }
    @Test
    public void orderValidTotalSumGreateThan5Test(){
        Order order = new Order();
        order.setHeader(1l);
        order.addLine(new OrderLine("A", 1));
        order.addLine(new OrderLine("B", 4));
        order.addLine(new OrderLine("C", 5));
        Assert.assertTrue("order must have all lines with a quantity between 1 and 5 inclusive",order.isValid());
    }
    @Test
    public void orderValidTwoLinesOfSameItemTest(){
        Order order = new Order();
        order.setHeader(1l);
        order.addLine(new OrderLine("A", 1));
        order.addLine(new OrderLine("B", 4));
        order.addLine(new OrderLine("B", 5));
        Assert.assertTrue("order must have all lines with a quantity between 1 and 5 inclusive",order.isValid());
    }
    @Test
    public void orderInvalidTest(){
        Order order = new Order();
        order.setHeader(1l);
        order.addLine(new OrderLine("A", 6));
        Assert.assertFalse("order must have all lines with a quantity between 1 and 5 inclusive", order.isValid());
    }
    @Test
    public void orderInvalid0Test(){
        Order order = new Order();
        order.setHeader(1l);
        order.addLine(new OrderLine("A", 0));
        Assert.assertFalse("order must have all lines with a quantity between 1 and 5 inclusive", order.isValid());
    }

    @Test
    public void orderStatTest(){
        OrderLine line = new OrderLine(null, 0);
        Assert.assertEquals("null:[0,0,0]", line.stat());

        line = new OrderLine("A", 0);
        Assert.assertEquals("A:[0,0,0]", line.stat());

        line = new OrderLine("B", 5);
        line.setStatus(OrderLine.Status.FILLED);
        Assert.assertEquals("B:[5,5,0]", line.stat());

        line = new OrderLine("C", 3);
        line.setStatus(OrderLine.Status.BACKORDERED);
        Assert.assertEquals("C:[3,0,3]", line.stat());

        line = new OrderLine("D", 2);
        Assert.assertEquals("D:[2,0,0]", line.stat());

    }
}
