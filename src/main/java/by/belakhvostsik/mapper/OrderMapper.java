package by.belakhvostsik.mapper;

import by.belakhvostsik.model.Order;
import by.belakhvostsik.dto.OrderDTO;
import org.mapstruct.Mapper;

@Mapper(uses = OrderDetailMapper.class)
public abstract class OrderMapper {

    public abstract OrderDTO toDTO(Order order);

    public abstract Order toEntity(OrderDTO dto);
}
