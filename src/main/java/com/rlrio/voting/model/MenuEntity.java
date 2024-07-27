package com.rlrio.voting.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.IOException;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Slf4j
@Data
@Entity
@Table(name = "menus")
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "menus_seq")
    @SequenceGenerator(name = "menus_seq", sequenceName = "menus_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurant;

    @Lob
    @Column(columnDefinition = "TEXT", name = "menu_items")
    private String menuItemsStr;

    @Transient
    private List<MenuItem> menuItems;

    @PostLoad
    private void postLoad() {
        if (!menuItemsStr.isBlank()) {
            try {
                var objectMapper = new ObjectMapper();
                this.menuItems = objectMapper.readValue(menuItemsStr,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, MenuItem.class));
            } catch (IOException e) {
                log.error("error with reading menu_items from database");
            }
        }
    }

    @PrePersist
    @PreUpdate
    private void prePersistUpdate() {
        if (!CollectionUtils.isEmpty(menuItems)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                this.menuItemsStr = objectMapper.writeValueAsString(menuItems);
            } catch (JsonProcessingException e) {
                log.error("error with writing menu_items to database");
            }
        }
    }
}
