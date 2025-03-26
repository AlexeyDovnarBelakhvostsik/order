package by.belakhvostsik.mapper;

import by.belakhvostsik.model.OrderDetail;
import by.belakhvostsik.dto.OrderDetailDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class OrderDetailMapper {

    public abstract OrderDetailDTO toDTO(OrderDetail detail);

    public abstract OrderDetail toEntity(OrderDetailDTO dto);
}
