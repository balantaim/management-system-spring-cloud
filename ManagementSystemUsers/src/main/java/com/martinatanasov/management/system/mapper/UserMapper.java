package com.martinatanasov.management.system.mapper;

import com.martinatanasov.management.system.users.User;
import com.martinatanasov.management.system.users.UserDetailsDto;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    UserDetailsDto userToUserDataDto(User user);

    User UserDataDtoToUser(UserDetailsDto userDetailsDto);

}
