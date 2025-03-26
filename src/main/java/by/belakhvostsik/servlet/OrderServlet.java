package by.belakhvostsik.servlet;

import by.belakhvostsik.dao.orderservice.OrderDAOImpl;
import by.belakhvostsik.dao.orderservice.impl.OrderDAO;
import by.belakhvostsik.dto.OrderDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/orders", "/orders/*"})
public class OrderServlet extends HttpServlet {

    public OrderDAO orderDAO = new OrderDAOImpl();
    private final ObjectMapper mapper = new ObjectMapper();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String json = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        OrderDTO orderDTO = mapper.readValue(json, OrderDTO.class);
        orderDAO.create(orderDTO);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("Заказ успешно создан.");
        resp.getWriter().close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String read;

        if (req.getPathInfo() != null) {
            Long id = Long.parseLong(req.getPathInfo().replace("/", ""));
            List<OrderDTO> readId = orderDAO.readId(id);
            read = mapper.writeValueAsString(readId);
        } else {
            List<OrderDTO> all = orderDAO.readAll();
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
        OrderDTO orderDTO = mapper.readValue(json, OrderDTO.class);
        orderDAO.update(orderDTO);

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Long id = Long.parseLong(req.getPathInfo().replace("/", ""));
        orderDAO.delete(id);

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
