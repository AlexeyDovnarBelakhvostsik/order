package by.belakhvostsik.dto;

import java.math.BigDecimal;

public class OrderDetailDTO {

    private Long id;
    private Long productSku;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private Long orderId;

    public OrderDetailDTO() {
    }

    public OrderDetailDTO(Long productSku, String productName, int quantity, BigDecimal unitPrice) {
        this.productSku = productSku;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public OrderDetailDTO(Long id, Long productSku, String productName, int quantity, BigDecimal unitPrice, Long orderId) {
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
}
