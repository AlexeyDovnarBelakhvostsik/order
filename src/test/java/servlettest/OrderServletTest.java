package servlettest;

import by.belakhvostsik.dao.orderservice.impl.OrderDAO;
import by.belakhvostsik.dto.OrderDTO;
import by.belakhvostsik.servlet.OrderServlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.servlet.http.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private OrderDAO orderDAO;

    @InjectMocks
    private TestableOrderServlet servlet;

    private StringWriter responseWriter;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        servlet = new TestableOrderServlet(orderDAO);
    }

    // Вспомогательный класс для доступа к protected методам
    private static class TestableOrderServlet extends OrderServlet {
        public TestableOrderServlet(OrderDAO orderDAO) {
            this.orderDAO = orderDAO;
        }

        @Override
        public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            super.doPost(req, resp);
        }

        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            super.doGet(req, resp);
        }

        @Override
        public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            super.doPut(req, resp);
        }

        @Override
        public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
            super.doDelete(req, resp);
        }
    }

    @Test
    void testDoPost() throws Exception {
        // Подготовка данных
        OrderDTO order = createTestOrder();
        String json = mapper.writeValueAsString(order);

        // Мокирование запроса
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        // Вызов метода
        servlet.doPost(request, response);

        // Проверки
        verify(orderDAO).create(argThat(dto ->
                dto.getOrderNumber().equals("ORD-001") &&
                        dto.getTotalAmount().equals(new BigDecimal("100.00"))
        ));

        assertEquals("Заказ успешно создан.", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testDoGetAll() throws Exception {
        // Подготовка данных
        List<OrderDTO> orders = Arrays.asList(
                createTestOrder(),
                createTestOrder()
        );
        when(orderDAO.readAll()).thenReturn(orders);

        // Вызов метода
        servlet.doGet(request, response);

        // Проверки
        String result = responseWriter.toString();
        OrderDTO[] resultOrders = mapper.readValue(result, OrderDTO[].class);

        assertEquals(2, resultOrders.length);
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testDoGetById() throws Exception {
        // Подготовка данных
        Long orderId = 1L;
        when(request.getPathInfo()).thenReturn("/1");
        when(orderDAO.readId(orderId)).thenReturn(Collections.singletonList(createTestOrder()));

        // Вызов метода
        servlet.doGet(request, response);

        // Проверки
        String result = responseWriter.toString();
        OrderDTO[] resultOrders = mapper.readValue(result, OrderDTO[].class);

        assertEquals(1, resultOrders.length);
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testDoPut() throws Exception {
        // Подготовка данных
        OrderDTO order = createTestOrder();
        String json = mapper.writeValueAsString(order);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        // Вызов метода
        servlet.doPut(request, response);

        // Проверки
        verify(orderDAO).update(argThat(dto ->
                dto.getId().equals(1L)
        ));
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDoDelete() {
        // Подготовка данных
        Long orderId = 1L;
        when(request.getPathInfo()).thenReturn("/1");

        // Вызов метода
        servlet.doDelete(request, response);

        // Проверки
        verify(orderDAO).delete(orderId);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private OrderDTO createTestOrder() {
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setOrderNumber("ORD-001");
        order.setTotalAmount(new BigDecimal("100.00"));
        return order;
    }
}
