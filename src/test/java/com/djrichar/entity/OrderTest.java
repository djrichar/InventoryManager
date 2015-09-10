package com.djrichar.entity;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by daniel on 8/27/15.
 */
public class OrderTest {

    @Test
    public void orderValidTest() {
        Order order = new Order();
        order.setHeader("1");
        order.addLine(new OrderLine("A", 1));
        order.addLine(new OrderLine("B", 4));

        Assert.assertTrue("entity must have all lines with a quantity between 1 and 5 inclusive", order.isValid());
    }
    @Test
    public void orderValidTotalSumGreateThan5Test(){
        Order order = new Order();
        order.setHeader("1");
        order.addLine(new OrderLine("A", 1));
        order.addLine(new OrderLine("B", 4));
        order.addLine(new OrderLine("C", 5));
        Assert.assertTrue("entity must have all lines with a quantity between 1 and 5 inclusive",order.isValid());
    }
    @Test
    public void orderValidTwoLinesOfSameItemTest(){
        Order order = new Order();
        order.setHeader("1");
        order.addLine(new OrderLine("A", 1));
        order.addLine(new OrderLine("B", 4));
        order.addLine(new OrderLine("B", 5));
        Assert.assertTrue("entity must have all lines with a quantity between 1 and 5 inclusive",order.isValid());
    }
    @Test
    public void orderInvalidTest(){
        Order order = new Order();
        order.setHeader("1");
        order.addLine(new OrderLine("A", 6));
        Assert.assertFalse("entity must have all lines with a quantity between 1 and 5 inclusive", order.isValid());
    }
    @Test
    public void orderInvalid0Test(){
        Order order = new Order();
        order.setHeader("1");
        order.addLine(new OrderLine("A", 0));
        Assert.assertFalse("entity must have all lines with a quantity between 1 and 5 inclusive", order.isValid());
    }

    @Test
    public void orderStringTest(){
        Order order = new Order();
        Assert.assertEquals("Order{hdr:null, lines:[]}", order.toString());

        order.setHeader("A%$null123");
        Assert.assertEquals("Order{hdr:A%$null123, lines:[]}", order.toString());

        order.setHeader("1");
        order.addLine(new OrderLine((InventoryItem) null, 9));
        order.addLine(new OrderLine(new InventoryItem("A"), 3));

        Assert.assertEquals("Order{hdr:1, lines:" +
                "[??:null, A:null]}",
                order.toString());
    }

    @Test
    public void orderLineStringTest(){
        OrderLine line = new OrderLine((String)null, 0);
        Assert.assertEquals("null:null", line.toString());

        line = new OrderLine((InventoryItem)null, 0);
        line.getFulfillment();
        Assert.assertEquals("??:{ q:0, b:0, o:0, stock:0, backordered:0}", line.toString());

        line = new OrderLine("A", 0);
        line.getFulfillment();
        Assert.assertEquals("A:{ q:0, b:0, o:0, stock:0, backordered:0}", line.toString());

        line = new OrderLine("B", 5);
        line.getFulfillment().setStatus(Fulfillment.Status.FILLED);
        Assert.assertEquals("B:{ q:5, b:0, o:5, stock:0, backordered:0}", line.toString());

        line = new OrderLine("C", 3);
        line.getFulfillment().setStatus(Fulfillment.Status.BACKORDERED);
        Assert.assertEquals("C:{ q:3, b:3, o:0, stock:0, backordered:0}", line.toString());

        line = new OrderLine("D", 2);
        line.getFulfillment();
        Assert.assertEquals("D:{ q:2, b:0, o:0, stock:0, backordered:0}", line.toString());

    }
}
