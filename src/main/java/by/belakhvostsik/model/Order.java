package by.belakhvostsik.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

public class Order {
    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private Date orderDate;
    private String recipient;
    private String deliveryAddress;
    private String paymentType;
    private String deliveryType;
    private List<OrderDetail> details;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public List<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(orderNumber, order.orderNumber) && Objects.equals(totalAmount, order.totalAmount) && Objects.equals(orderDate, order.orderDate) && Objects.equals(recipient, order.recipient) && Objects.equals(deliveryAddress, order.deliveryAddress) && Objects.equals(paymentType, order.paymentType) && Objects.equals(deliveryType, order.deliveryType) && Objects.equals(details, order.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderNumber, totalAmount, orderDate, recipient, deliveryAddress, paymentType, deliveryType, details);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", totalAmount=" + totalAmount +
                ", orderDate=" + orderDate +
                ", recipient='" + recipient + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", deliveryType='" + deliveryType + '\'' +
                ", details=" + details +
                '}';
    }
}
