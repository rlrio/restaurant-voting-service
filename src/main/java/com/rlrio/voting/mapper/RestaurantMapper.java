package com.rlrio.voting.mapper;

import com.rlrio.voting.controller.dto.restaurant.RestaurantCreateOrUpdateDto;
import com.rlrio.voting.controller.dto.restaurant.RestaurantDto;
import com.rlrio.voting.controller.dto.restaurant.RestaurantFullInfo;
import com.rlrio.voting.model.MenuEntity;
import com.rlrio.voting.model.RestaurantEntity;
import com.rlrio.voting.model.VoteEntity;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import static java.util.Collections.emptyList;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantMapper {
    RestaurantMapper INSTANCE = Mappers.getMapper(RestaurantMapper.class);

    RestaurantDto toDto(RestaurantEntity entity);

    RestaurantEntity toEntity(RestaurantCreateOrUpdateDto dto);

    @Mapping(target = "id", source = "restaurant.id")
    @Mapping(target = "name", source = "restaurant.name")
    @Mapping(target = "menuItems", source = "menu.menuItems")
    RestaurantFullInfo toFullInfoDto(RestaurantEntity restaurant, MenuEntity menu);


    default List<RestaurantFullInfo> toFullInfoDto(List<RestaurantEntity> restaurantEntities,
                                                   Map<Long, MenuEntity> restaurantIdToMenuItems) {
        return restaurantEntities.stream()
                .map(it -> toFullInfoDto(it, restaurantIdToMenuItems.get(it.getId())))
                .toList();
    }
}
