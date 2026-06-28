package com.martinatanasov.management.system.mappers;

import com.martinatanasov.management.system.users.User;
import com.martinatanasov.management.system.users.UserAnalyticsDetailsDto;
import com.martinatanasov.management.system.users.UserDetailsDto;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    UserDetailsDto userToUserDataDto(User user);

    UserAnalyticsDetailsDto userToUserAnalyticsDetailsDto(User user);

    User UserDataDtoToUser(UserDetailsDto userDetailsDto);

}
