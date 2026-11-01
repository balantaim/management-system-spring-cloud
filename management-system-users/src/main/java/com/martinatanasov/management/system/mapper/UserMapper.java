package com.martinatanasov.management.system.mapper;

import com.martinatanasov.management.system.user.User;
import com.martinatanasov.management.system.user.UserAnalyticsDetailsDto;
import com.martinatanasov.management.system.user.UserDetailsDto;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    UserDetailsDto userToUserDataDto(User user);

    UserAnalyticsDetailsDto userToUserAnalyticsDetailsDto(User user);

    User UserDataDtoToUser(UserDetailsDto userDetailsDto);

}
