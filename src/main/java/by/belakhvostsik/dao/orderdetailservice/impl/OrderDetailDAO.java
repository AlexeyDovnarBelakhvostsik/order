package by.belakhvostsik.dao.orderdetailservice.impl;

import by.belakhvostsik.dto.OrderDetailDTO;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailDAO {

    void create(OrderDetailDTO dto);

    List<OrderDetailDTO> readId(Long id);

    List<OrderDetailDTO> readAll();

    void update(OrderDetailDTO dto);

    void delete(Long id);
}
