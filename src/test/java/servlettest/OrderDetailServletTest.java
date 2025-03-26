package servlettest;

import by.belakhvostsik.dao.orderdetailservice.impl.OrderDetailDAO;
import by.belakhvostsik.dto.OrderDetailDTO;
import by.belakhvostsik.servlet.OrderDetailServlet;
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
class OrderDetailServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private OrderDetailDAO orderDetailDAO;

    @InjectMocks
    private TestableOrderDetailServlet servlet;

    private StringWriter responseWriter;
    private ObjectMapper mapper = new ObjectMapper();

    // Вспомогательный класс для доступа к protected методам
    private static class TestableOrderDetailServlet extends OrderDetailServlet {
        public TestableOrderDetailServlet(OrderDetailDAO orderDetailDAO) {
            this.orderDetailDAO = orderDetailDAO;
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

    @BeforeEach
    void setUp() throws IOException {
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        servlet = new TestableOrderDetailServlet(orderDetailDAO);
    }

    @Test
    void shouldCreateOrderDetail() throws Exception {
        OrderDetailDTO input = createTestDetail();
        String json = mapper.writeValueAsString(input);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        servlet.doPost(request, response);

        verify(orderDetailDAO).create(argThat(dto ->
                dto.getProductSku() == 999L &&
                        dto.getQuantity() == 2
        ));

        assertEquals("Детали к заказу успешно создан.", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void shouldGetAllDetails() throws Exception {
        List<OrderDetailDTO> details = Arrays.asList(
                createTestDetail(),
                createTestDetail()
        );
        when(orderDetailDAO.readAll()).thenReturn(details);

        servlet.doGet(request, response);

        OrderDetailDTO[] result = mapper.readValue(responseWriter.toString(), OrderDetailDTO[].class);
        assertEquals(2, result.length);
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void shouldGetDetailById() throws Exception {
        Long detailId = 5L;
        when(request.getPathInfo()).thenReturn("/5");
        when(orderDetailDAO.readId(detailId)).thenReturn(Collections.singletonList(createTestDetail()));

        servlet.doGet(request, response);

        OrderDetailDTO[] result = mapper.readValue(responseWriter.toString(), OrderDetailDTO[].class);
        assertEquals(1, result.length);
    }

    @Test
    void shouldUpdateDetail() throws Exception {
        OrderDetailDTO input = createTestDetail();
        String json = mapper.writeValueAsString(input);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        servlet.doPut(request, response);

        verify(orderDetailDAO).update(argThat(dto ->
                dto.getId().equals(1L)
        ));
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void shouldDeleteDetail() throws Exception {
        Long detailId = 7L;
        when(request.getPathInfo()).thenReturn("/7");

        servlet.doDelete(request, response);

        verify(orderDetailDAO).delete(detailId);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private OrderDetailDTO createTestDetail() {
        OrderDetailDTO detail = new OrderDetailDTO();
        detail.setId(1L);
        detail.setProductSku(999L);
        detail.setProductName("Test Product");
        detail.setQuantity(2);
        detail.setUnitPrice(new BigDecimal("49.99"));
        detail.setOrderId(100L);
        return detail;
    }
}
