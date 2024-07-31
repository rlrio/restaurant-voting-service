package com.rlrio.voting.mapper;

import com.rlrio.voting.controller.dto.auth.UserDto;
import com.rlrio.voting.model.Role;
import com.rlrio.voting.model.UserEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity toUserEntity(UserDto userDto);

    @AfterMapping
    default void setRole(@MappingTarget UserEntity userEntity) {
        userEntity.setRole(Role.USER);
    }
}
