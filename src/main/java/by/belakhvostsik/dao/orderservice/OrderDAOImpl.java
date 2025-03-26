package by.belakhvostsik.dao.orderservice;

import by.belakhvostsik.dao.orderservice.impl.OrderDAO;
import by.belakhvostsik.db.DatabaseConnection;
import by.belakhvostsik.dto.OrderDTO;
import by.belakhvostsik.dto.OrderDetailDTO;
import by.belakhvostsik.mapper.OrderDetailMapper;
import by.belakhvostsik.mapper.OrderDetailMapperImpl;
import by.belakhvostsik.mapper.OrderMapper;
import by.belakhvostsik.mapper.OrderMapperImpl;
import by.belakhvostsik.model.Order;
import by.belakhvostsik.model.OrderDetail;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDAOImpl implements OrderDAO {

    static final String CREATE_ORDER = "insert into orders.orders (order_number, total_amount, order_date, recipient, " +
            "delivery_address, payment_type, delivery_type) values ( ?,?,?,?,?,?,?)";

    static final String CREATE_ORDER_DETAIL = "insert into orders.order_details (product_sku, product_name, quantity, " +
            "unit_price, order_id) values ( ?,?,?,?,?)";

    static final String UPDATE_ORDER = "update orders.orders set order_number = ?, total_amount = ?," +
            " recipient = ?, delivery_address = ?, payment_type = ?, delivery_type = ? where id = ?";

    static final String UPDATE_ORDER_DETAIL = "update orders.order_details set product_sku = ?, product_name = ?, quantity = ?," +
            " unit_price = ? where id = ?";

    static final String READ_ID = "SELECT o.id AS order_id, o.order_number, o.total_amount," +
            " o.order_date, "
            + "o.recipient, o.delivery_address, o.payment_type, o.delivery_type, "
            + "od.id AS detail_id, od.product_sku, od.product_name," +
            " od.quantity, od.unit_price "
            + "FROM orders.orders o "
            + "LEFT JOIN orders.order_details od ON o.id = od.order_id "
            + "WHERE o.id = ?";

    static final String READ_ALL = "SELECT o.id, o.order_number, o.total_amount, o.order_date, "
            + "o.recipient, o.delivery_address, o.payment_type, o.delivery_type, "
            + "od.id AS detail_id, od.product_sku, od.product_name, od.quantity, od.unit_price "
            + "FROM orders.orders o "
            + "LEFT JOIN orders.order_details od ON o.id = od.order_id";

    static final String DELETE = "delete from orders.orders where id = ?";

    private final OrderMapper orderMapper = new OrderMapperImpl();

    private final OrderDetailMapper orderDetailMapper = new OrderDetailMapperImpl();

    private static final Logger logger = Logger.getLogger(OrderDAOImpl.class.getName());

    @Override
    public void create(OrderDTO dto) {
        DatabaseConnection.initDriver();
        Order order = orderMapper.toEntity(dto);

        long returnIdContact = 0;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(CREATE_ORDER, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, order.getOrderNumber());
            pst.setBigDecimal(2, order.getTotalAmount());
            pst.setDate(3, Date.valueOf(LocalDate.now()));
            pst.setString(4, order.getRecipient());
            pst.setString(5, order.getDeliveryAddress());
            pst.setString(6, order.getPaymentType());
            pst.setString(7, order.getDeliveryType());
            pst.executeUpdate();

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    returnIdContact = generatedKeys.getLong(1);
                }
            }

            try (PreparedStatement pst1 = con.prepareStatement(CREATE_ORDER_DETAIL)) {
                for (OrderDetail orderDetail : order.getDetails()) {
                    pst1.setLong(1, orderDetail.getProductSku());
                    pst1.setString(2, orderDetail.getProductName());
                    pst1.setInt(3, orderDetail.getQuantity());
                    pst1.setBigDecimal(4, orderDetail.getUnitPrice());
                    pst1.setLong(5, returnIdContact);
                    pst1.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Поймано исключение", e);
        }

    }

    @Override
    public List<OrderDTO> readId(Long id) {
        DatabaseConnection.initDriver();
        ArrayList<OrderDTO> list = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(READ_ID)) {

            pst.setLong(1, id);
            ResultSet rs = pst.executeQuery();
            Order order = null;
            OrderDetail detail;
            List<OrderDetail> details = new ArrayList<>();

            while (rs.next()) {
                if (order == null) {
                    order = new Order();
                    order.setId(rs.getLong("order_id"));
                    createOrder(rs, order);
                }
                if (rs.getObject("detail_id") != null) {
                    detail = new OrderDetail();
                    detail.setId(rs.getLong("detail_id"));
                    detail.setProductSku(rs.getLong("product_sku"));
                    detail.setProductName(rs.getString("product_name"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getBigDecimal("unit_price"));
                    detail.setOrderId(rs.getLong("order_id"));
                    details.add(detail);
                }
            }
            if (order != null) {
                order.setDetails(details);
                list.add(orderMapper.toDTO(order));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Поймано исключение", e);
        }
        return list;
    }

    private void createOrder(ResultSet rs, Order order) throws SQLException {
        order.setOrderNumber(rs.getString("order_number"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setOrderDate(Date.valueOf(rs.getDate("order_date").toLocalDate()));
        order.setRecipient(rs.getString("recipient"));
        order.setDeliveryAddress(rs.getString("delivery_address"));
        order.setPaymentType(rs.getString("payment_type"));
        order.setDeliveryType(rs.getString("delivery_type"));
    }

    @Override
    public List<OrderDTO> readAll() {
        DatabaseConnection.initDriver();

        ArrayList<OrderDTO> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             Statement pst = con.createStatement(); ResultSet rs = pst.executeQuery(READ_ALL)) {

            Order currentOrder = null;
            OrderDTO currentOrderDTO = null;
            long lastOrderId = -1;

            while (rs.next()) {
                long orderId = rs.getLong("id");

                if (currentOrder == null || orderId != lastOrderId) {
                    currentOrder = new Order();
                    currentOrder.setId(orderId);
                    createOrder(rs, currentOrder);
                    currentOrder.setDetails(new ArrayList<>());

                    currentOrderDTO = orderMapper.toDTO(currentOrder);
                    list.add(currentOrderDTO);
                }

                if (rs.getObject("detail_id") != null) {
                    OrderDetail detail = new OrderDetail();
                    detail.setId(rs.getLong("detail_id"));
                    detail.setProductSku(rs.getLong("product_sku"));
                    detail.setProductName(rs.getString("product_name"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getBigDecimal("unit_price"));

                    OrderDetailDTO orderDetailDTO = orderDetailMapper.toDTO(detail);
                    currentOrderDTO.getDetails().add(orderDetailDTO);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Поймано исключение", e);
        }

        return list;
    }

    @Override
    public void update(OrderDTO dto) {
        DatabaseConnection.initDriver();
        Order order = orderMapper.toEntity(dto);

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(UPDATE_ORDER)) {

            pst.setLong(7, order.getId());
            pst.setString(1, order.getOrderNumber());
            pst.setBigDecimal(2, order.getTotalAmount());
            pst.setString(3, order.getRecipient());
            pst.setString(4, order.getDeliveryAddress());
            pst.setString(5, order.getPaymentType());
            pst.setString(6, order.getDeliveryType());
            pst.executeUpdate();

            try (PreparedStatement pst1 = con.prepareStatement(UPDATE_ORDER_DETAIL)) {
                for (OrderDetail orderDetail : order.getDetails()) {
                    pst1.setLong(1, orderDetail.getProductSku());
                    pst1.setString(2, orderDetail.getProductName());
                    pst1.setInt(3, orderDetail.getQuantity());
                    pst1.setBigDecimal(4, orderDetail.getUnitPrice());
                    pst1.setLong(5, order.getId());
                    pst1.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Поймано исключение", e);
        }

    }

    @Override
    public void delete(Long id) {
        DatabaseConnection.initDriver();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(DELETE)) {

            pst.setLong(1, id);
            pst.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Поймано исключение", e);
        }
    }
}
