package lux.fm.bookingservice.mapper;

import java.util.stream.Collectors;
import lux.fm.bookingservice.config.MapperConfig;
import lux.fm.bookingservice.dto.user.UpdateUserRequestDto;
import lux.fm.bookingservice.dto.user.UserRegistrationRequestDto;
import lux.fm.bookingservice.dto.user.UserResponseDto;
import lux.fm.bookingservice.dto.user.UserResponseDtoWithRoles;
import lux.fm.bookingservice.model.Role;
import lux.fm.bookingservice.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toUser(UserRegistrationRequestDto request);

    UserResponseDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapUpdateRequestToUser(
            UpdateUserRequestDto updateBookRequestDto, @MappingTarget User user
    );

    @AfterMapping
    default void setRoleIds(@MappingTarget UserResponseDtoWithRoles bookDto, User user) {
        bookDto.setRoleIds(user.getRoles()
                .stream()
                .map(Role::getId)
                .collect(Collectors.toSet())
        );
    }
}
