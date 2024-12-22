package com.rlrio.voting.service;

import com.rlrio.voting.controller.dto.PageResponse;
import com.rlrio.voting.controller.dto.restaurant.MenuDto;
import com.rlrio.voting.controller.dto.restaurant.RestaurantCreateOrUpdateDto;
import com.rlrio.voting.controller.dto.restaurant.RestaurantDto;
import com.rlrio.voting.controller.dto.restaurant.RestaurantFilter;
import com.rlrio.voting.controller.dto.restaurant.RestaurantFullInfo;
import com.rlrio.voting.mapper.RestaurantMapper;
import com.rlrio.voting.model.MenuEntity;
import com.rlrio.voting.model.RestaurantEntity;
import com.rlrio.voting.repository.RestaurantRepository;
import com.rlrio.voting.repository.VoteRepository;
import com.rlrio.voting.repository.specification.RestaurantSpecification;
import com.rlrio.voting.service.exception.NotFoundException;
import com.rlrio.voting.service.exception.VotingException;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final VoteRepository voteRepository;
    private final MenuService menuService;

    public RestaurantDto create(RestaurantCreateOrUpdateDto dto) {
        try {
            var entity = RestaurantMapper.INSTANCE.toEntity(dto);
            return RestaurantMapper.INSTANCE.toDto(restaurantRepository.save(entity));
        } catch (DataIntegrityViolationException e) {
            throw new VotingException(format("restaurant with name {0} already exists", dto.getName()));
        }
    }

    @Transactional
    public RestaurantDto update(Long id, RestaurantCreateOrUpdateDto dto) {
        var entity = findById(id);
        entity.setName(dto.getName());
        return RestaurantMapper.INSTANCE.toDto(restaurantRepository.save(entity));
    }

    @Transactional
    public RestaurantFullInfo setMenu(Long restaurantId, MenuDto menuDto) {
        var restaurant = findById(restaurantId);
        var menu = menuService.findByRestaurantId(restaurantId)
                .orElse(new MenuEntity().setRestaurant(restaurant));
        menu.setMenuItems(menuDto.getMenuItems());
        menuService.save(menu);
        return RestaurantMapper.INSTANCE.toFullInfoDto(restaurant, menu);
    }

    public RestaurantEntity findById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(format("restaurant with id {0} is not found", id)));
    }

    public PageResponse<RestaurantFullInfo> findAllByFilter(RestaurantFilter filter) {
        var restaurantsResponse = restaurantRepository.findAll(
                Specification
                        .where(RestaurantSpecification.hasMoreVotesThan(filter.getMinVotes())
                        .and(RestaurantSpecification.nameStartsWith(filter.getNameStartsWith()))),
                PageRequest.of(filter.getPageNumber(), filter.getPageSize()));
        var restaurantIds = restaurantsResponse.getContent().stream()
                .map(RestaurantEntity::getId)
                .toList();

        var menusByRestaurantId = menuService.findByRestaurantIds(restaurantIds).stream()
                .collect(toMap(it -> it.getRestaurant().getId(), Function.identity()));

        var fullInfoList = RestaurantMapper.INSTANCE.toFullInfoDto(restaurantsResponse.getContent(), menusByRestaurantId);

        return new PageResponse<RestaurantFullInfo>()
                .setContent(fullInfoList)
                .setTotalSize(restaurantsResponse.getTotalElements())
                .setTotalPages(restaurantsResponse.getTotalPages());
    }
}
