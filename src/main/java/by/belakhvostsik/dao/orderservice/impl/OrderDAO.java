package by.belakhvostsik.dao.orderservice.impl;

import by.belakhvostsik.dto.OrderDTO;

import java.util.List;

public interface OrderDAO {
    void create(OrderDTO dto);

    List<OrderDTO> readId(Long id);

    List<OrderDTO> readAll();

    void update(OrderDTO dto);

    void delete(Long id);
}
