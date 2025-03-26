package daotest;

import by.belakhvostsik.dao.orderdetailservice.OrderDetailDAOImpl;
import by.belakhvostsik.dto.OrderDetailDTO;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class OrderDetailsDAOImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("admin")
            .withExposedPorts(5432)
            .withCreateContainerCmdModifier(cmd ->
                    cmd.withHostConfig(new HostConfig()
                            .withPortBindings(new PortBinding(Ports.Binding.bindPort(5432), new ExposedPort(5432)))
                    )
            );

    private static OrderDetailDAOImpl orderDetailDAO;

    @BeforeAll
    static void setUp() throws SQLException {
        // Устанавливаем тестовые настройки подключения
        DatabaseConnection.setTestConfig(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        // Создание схемы и таблиц
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE SCHEMA IF NOT EXISTS orders;");

            stmt.execute("CREATE TABLE IF NOT EXISTS orders.orders (" +
                    "id SERIAL PRIMARY KEY)");

            stmt.execute("CREATE TABLE orders.order_details (" +
                    "id SERIAL PRIMARY KEY, " +
                    "product_sku BIGINT, " +
                    "product_name VARCHAR(100), " +
                    "quantity INT, " +
                    "unit_price NUMERIC, " +
                    "order_id INT REFERENCES orders.orders(id) ON DELETE CASCADE)");
        }

        // Инициализация DAO
        orderDetailDAO = new OrderDetailDAOImpl();
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE orders.order_details, orders.orders RESTART IDENTITY CASCADE;");
        }
    }

    private Long createTestOrder() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO orders.orders DEFAULT VALUES", Statement.RETURN_GENERATED_KEYS);
            var rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getLong(1);
        }
    }

    @Test
    void testCreateOrderDetail() throws SQLException {
        // Создаем тестовый заказ
        Long orderId = createTestOrder();

        // Создаем DTO
        OrderDetailDTO dto = createTestOrderDetail(orderId);

        // Тестируем создание
        orderDetailDAO.create(dto);

        // Проверяем чтение
        List<OrderDetailDTO> details = orderDetailDAO.readAll();
        assertEquals(1, details.size());
        assertEquals("Test Product", details.getFirst().getProductName());
    }

    @Test
    void testReadOrderDetailById() throws SQLException {
        // Создаем тестовый заказ
        Long orderId = createTestOrder();
        OrderDetailDTO orderDetailDTO = createTestOrderDetail(orderId);
        orderDetailDAO.create(orderDetailDTO);

        // Получаем ID созданного заказа
        List<OrderDetailDTO> orderDetails = orderDetailDAO.readAll();
        Long detailId  = orderDetails.getFirst().getId();

        // Тестируем чтение по ID
        List<OrderDetailDTO> result = orderDetailDAO.readId(detailId);
        assertFalse(result.isEmpty());
        assertEquals(detailId, result.getFirst().getId());
    }

    @Test
    void testReadAllOrdersDetails() throws SQLException {
        Long orderId = createTestOrder();
        orderDetailDAO.create(createTestOrderDetail(orderId));
        orderDetailDAO.create(createTestOrderDetail(orderId));

        List<OrderDetailDTO> details = orderDetailDAO.readAll();
        assertEquals(2, details.size());
    }

    @Test
    void testUpdateOrderDetail() throws SQLException {
        Long orderId = createTestOrder();
        OrderDetailDTO dto = createTestOrderDetail(orderId);
        orderDetailDAO.create(dto);

        List<OrderDetailDTO> details = orderDetailDAO.readAll();
        Long detailId = details.getFirst().getId();

        // Обновляем данные
        dto.setId(detailId);
        dto.setProductName("Updated Product");
        orderDetailDAO.update(dto);

        // Проверяем обновление
        List<OrderDetailDTO> updatedDetails = orderDetailDAO.readId(detailId);
        assertEquals("Updated Product", updatedDetails.getFirst().getProductName());
    }

    @Test
    void testDeleteOrderDetail() throws SQLException {
        Long orderId = createTestOrder();
        OrderDetailDTO dto = createTestOrderDetail(orderId);
        orderDetailDAO.create(dto);

        List<OrderDetailDTO> details = orderDetailDAO.readAll();
        Long detailId = details.getFirst().getId();

        // Удаляем запись
        orderDetailDAO.delete(detailId);

        // Проверяем удаление
        List<OrderDetailDTO> result = orderDetailDAO.readId(detailId);
        assertTrue(result.isEmpty());
    }

    private OrderDetailDTO createTestOrderDetail(Long orderId) {
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        orderDetailDTO.setProductSku(4L);
        orderDetailDTO.setProductName("Test Product");
        orderDetailDTO.setQuantity(2);
        orderDetailDTO.setUnitPrice(BigDecimal.valueOf(100.50));
        orderDetailDTO.setOrderId(orderId);

        return orderDetailDTO;
    }
}

