package by.belakhvostsik.model;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderDetail {

    private Long id;
    private Long productSku;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private Long orderId;

    public OrderDetail() {
    }

    public OrderDetail(Long id, Long productSku, String productName, int quantity, BigDecimal unitPrice, Long orderId) {
        this.id = id;
        this.productSku = productSku;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.orderId = orderId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductSku() {
        return productSku;
    }

    public void setProductSku(Long productSku) {
        this.productSku = productSku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetail that = (OrderDetail) o;
        return quantity == that.quantity && Objects.equals(id, that.id) && Objects.equals(productSku, that.productSku) && Objects.equals(productName, that.productName) && Objects.equals(unitPrice, that.unitPrice) && Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productSku, productName, quantity, unitPrice, orderId);
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "id=" + id +
                ", productSku=" + productSku +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", orderId=" + orderId +
                '}';
    }
}
