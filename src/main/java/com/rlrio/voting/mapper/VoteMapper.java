package com.rlrio.voting.mapper;

import com.rlrio.voting.controller.dto.vote.CountVoteDto;
import com.rlrio.voting.model.RestaurantVoteCount;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VoteMapper {
    VoteMapper INSTANCE = Mappers.getMapper(VoteMapper.class);

    @Mapping(target = "restaurantId", source = "entity.restaurantId")
    @Mapping(target = "votes", source = "entity.voteCount")
    CountVoteDto toDto(RestaurantVoteCount entity);

    List<CountVoteDto> toDto(List<RestaurantVoteCount> votesToRestaurantId);
}
