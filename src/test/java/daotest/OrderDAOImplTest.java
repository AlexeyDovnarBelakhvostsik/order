package daotest;

import by.belakhvostsik.dao.orderservice.OrderDAOImpl;
import by.belakhvostsik.dto.OrderDTO;
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
class OrderDAOImplTest {

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

    private static OrderDAOImpl orderDAO;

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

            stmt.execute("CREATE TABLE orders.orders (" +
                    "id SERIAL PRIMARY KEY, " +
                    "order_number VARCHAR(50), " +
                    "total_amount NUMERIC, " +
                    "order_date DATE, " +
                    "recipient VARCHAR(100), " +
                    "delivery_address VARCHAR(200), " +
                    "payment_type VARCHAR(50), " +
                    "delivery_type VARCHAR(50))");

            stmt.execute("CREATE TABLE orders.order_details (" +
                    "id SERIAL PRIMARY KEY, " +
                    "product_sku BIGINT, " +
                    "product_name VARCHAR(100), " +
                    "quantity INT, " +
                    "unit_price NUMERIC, " +
                    "order_id INT REFERENCES orders.orders(id) ON DELETE CASCADE )");
        }

        // Инициализация DAO
        orderDAO = new OrderDAOImpl();
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE orders.orders, orders.order_details RESTART IDENTITY CASCADE;");
        }
    }

    @Test
    void testCreateOrder() {
        OrderDTO orderDTO = createTestOrder();

        // Вызов тестируемого метода
        orderDAO.create(orderDTO);

        // Проверка, что заказ создан
        List<OrderDTO> orders = orderDAO.readAll();
        assertEquals(1, orders.size());
        assertEquals("TEST-001", orders.getFirst().getOrderNumber());
    }

    @Test
    void testReadOrderById() {
        // Создаем тестовый заказ
        OrderDTO orderDTO = createTestOrder();
        orderDAO.create(orderDTO);

        // Получаем ID созданного заказа
        List<OrderDTO> orders = orderDAO.readAll();
        Long orderId = orders.getFirst().getId();

        // Тестируем чтение по ID
        List<OrderDTO> result = orderDAO.readId(orderId);
        assertFalse(result.isEmpty());
        assertEquals("TEST-001", result.getFirst().getOrderNumber());
    }

    @Test
    void testReadAllOrders() {
        // Создаем два тестовых заказа
        orderDAO.create(createTestOrder());
        orderDAO.create(createTestOrder());

        // Проверяем, что оба заказа есть в базе
        List<OrderDTO> orders = orderDAO.readAll();
        assertEquals(2, orders.size());
    }

    @Test
    void testUpdateOrder() {
        // Создаем заказ
        OrderDTO orderDTO = createTestOrder();
        orderDAO.create(orderDTO);

        // Получаем ID
        Long orderId = orderDAO.readAll().getFirst().getId();
        orderDTO.setId(orderId);
        orderDTO.setRecipient("Новый получатель");
        orderDTO.getDetails().getFirst().setQuantity(5);

        // Обновляем заказ
        orderDAO.update(orderDTO);

        // Проверяем изменения
        OrderDTO updatedOrder = orderDAO.readId(orderId).getFirst();
        assertEquals("Новый получатель", updatedOrder.getRecipient());
    }

    @Test
    void testDeleteOrder() {
        // Создаем заказ
        OrderDTO orderDTO = createTestOrder();
        orderDAO.create(orderDTO);

        // Получаем ID
        Long orderId = orderDAO.readAll().getFirst().getId();

        // Удаляем заказ
        orderDAO.delete(orderId);

        // Проверяем, что заказ удален
        List<OrderDTO> result = orderDAO.readId(orderId);
        assertTrue(result.isEmpty());
    }

    private OrderDTO createTestOrder() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderNumber("TEST-001");
        orderDTO.setTotalAmount(new BigDecimal("200.00"));
        orderDTO.setRecipient("Петр Петров");
        orderDTO.setDeliveryAddress("пр. Тестовый, 2");
        orderDTO.setPaymentType("CASH");
        orderDTO.setDeliveryType("STANDARD");

        orderDTO.setDetails(List.of(
                new OrderDetailDTO(4L, "Деталь", 123, BigDecimal.valueOf(67))
        ));
        return orderDTO;
    }
}