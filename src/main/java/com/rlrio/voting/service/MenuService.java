package com.rlrio.voting.service;

import com.rlrio.voting.model.MenuEntity;
import com.rlrio.voting.repository.MenuRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    public Optional<MenuEntity> findByRestaurantId(Long restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }

    public MenuEntity save(MenuEntity menuEntity) {
        return menuRepository.save(menuEntity);
    }
}
