package by.belakhvostsik.servlet;

import by.belakhvostsik.dao.orderdetailservice.OrderDetailDAOImpl;
import by.belakhvostsik.dao.orderdetailservice.impl.OrderDetailDAO;
import by.belakhvostsik.dto.OrderDetailDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/details", "/details/*"})
public class OrderDetailServlet extends HttpServlet {

    public OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String json = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        OrderDetailDTO orderDetailDTO = mapper.readValue(json, OrderDetailDTO.class);
        orderDetailDAO.create(orderDetailDTO);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("Детали к заказу успешно создан.");
        resp.getWriter().close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String read;

        if (req.getPathInfo() != null) {
            Long id = Long.parseLong(req.getPathInfo().replace("/", ""));
            List<OrderDetailDTO> readId = orderDetailDAO.readId(id);
            read = mapper.writeValueAsString(readId);
        } else {
            List<OrderDetailDTO> all = orderDetailDAO.readAll();
            read = mapper.writeValueAsString(all);

        }
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(read);
        resp.getWriter().close();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String json = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        OrderDetailDTO orderDetailDTO = mapper.readValue(json, OrderDetailDTO.class);
        orderDetailDAO.update(orderDetailDTO);

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Long id = Long.parseLong(req.getPathInfo().replace("/", ""));
        orderDetailDAO.delete(id);

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

    }
}
