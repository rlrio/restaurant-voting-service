package com.rlrio.voting.mapper;

import com.rlrio.voting.controller.dto.vote.CountVoteDto;
import com.rlrio.voting.model.VoteEntity;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import static java.util.Comparator.comparing;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VoteMapper {
    VoteMapper INSTANCE = Mappers.getMapper(VoteMapper.class);

    @Mapping(target = "restaurantId", source = "restaurantId")
    @Mapping(target = "votes", expression = "java(votes != null ? votes.size() : 0)")
    CountVoteDto toDto(Long restaurantId, List<VoteEntity> votes);

    default List<CountVoteDto> toDto(Map<Long, List<VoteEntity>> votesToRestaurantId) {
        return votesToRestaurantId.entrySet().stream()
                .map(entry -> toDto(entry.getKey(), entry.getValue()))
                .sorted(comparing(CountVoteDto::getRestaurantId))
                .toList();
    }
}
