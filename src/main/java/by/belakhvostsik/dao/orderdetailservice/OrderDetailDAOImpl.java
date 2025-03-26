package by.belakhvostsik.dao.orderdetailservice;

import by.belakhvostsik.dao.orderdetailservice.impl.OrderDetailDAO;
import by.belakhvostsik.db.DatabaseConnection;
import by.belakhvostsik.dto.OrderDetailDTO;
import by.belakhvostsik.mapper.OrderDetailMapper;
import by.belakhvostsik.mapper.OrderDetailMapperImpl;
import by.belakhvostsik.model.OrderDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDetailDAOImpl implements OrderDetailDAO {

    static final String CREATE_ORDER_DETAILS = "insert into orders.order_details (product_sku, product_name, quantity, " +
            "unit_price, order_id) values ( ?,?,?,?,?)";

    static final String UPDATE_ORDER_DETAILS = "update orders.order_details set product_sku = ?, product_name = ?, quantity = ?," +
            " unit_price = ? where id = ?";

    static final String READ_ID = "select * from orders.order_details where id = ?";

    static final String READ_ALL = "select * from orders.order_details ";


    static final String DELETE = "delete from orders.order_details where id = ?";

    private final OrderDetailMapper orderDetailMapper = new OrderDetailMapperImpl();

    private static final Logger logger = Logger.getLogger(OrderDetailDAOImpl.class.getName());

    @Override
    public void create(OrderDetailDTO dto){
        DatabaseConnection.initDriver();
        OrderDetail orderDetail = orderDetailMapper.toEntity(dto);

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(CREATE_ORDER_DETAILS)) {

            pst.setLong(1, orderDetail.getProductSku());
            pst.setString(2, orderDetail.getProductName());
            pst.setInt(3, orderDetail.getQuantity());
            pst.setBigDecimal(4, orderDetail.getUnitPrice());
            pst.setLong(5, orderDetail.getOrderId());
            pst.executeUpdate();


        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Поймано исключение", e);
        }

    }

    @Override
    public List<OrderDetailDTO> readId(Long id)  {
        DatabaseConnection.initDriver();
        ArrayList<OrderDetailDTO> list = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(READ_ID)) {

            pst.setLong(1, id);
            findFromDB(list, pst);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Поймано исключение", e);
        }

        return list;
    }

    private void findFromDB(ArrayList<OrderDetailDTO> list, PreparedStatement pst) throws SQLException {
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            OrderDetail orderDetail = new OrderDetail(rs.getLong("id"),
                    rs.getLong("product_sku"),
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("unit_price"),
                    rs.getLong("order_id"));
            list.add(orderDetailMapper.toDTO(orderDetail));
        }
    }

    @Override
    public List<OrderDetailDTO> readAll() {
        DatabaseConnection.initDriver();
        ArrayList<OrderDetailDTO> list = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(READ_ALL)) {

            findFromDB(list, pst);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Поймано исключение", e);
        }
        return list;
    }

    @Override
    public void update(OrderDetailDTO dto){
        DatabaseConnection.initDriver();
        OrderDetail orderDetail = orderDetailMapper.toEntity(dto);

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(UPDATE_ORDER_DETAILS)) {

            pst.setLong(5, orderDetail.getId());
            pst.setLong(1, orderDetail.getProductSku());
            pst.setString(2, orderDetail.getProductName());
            pst.setInt(3, orderDetail.getQuantity());
            pst.setBigDecimal(4, orderDetail.getUnitPrice());
            pst.executeUpdate();

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
