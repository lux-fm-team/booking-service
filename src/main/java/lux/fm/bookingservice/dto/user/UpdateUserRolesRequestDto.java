package lux.fm.bookingservice.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

public class UpdateUserRolesRequestDto {
        @Getter
        @Setter
        @NotNull
        @NotEmpty(message = "You must pass at least one role id")
        private Set<Long> roleIds;
}
