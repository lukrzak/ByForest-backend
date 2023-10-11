package com.lukrzak.ByForest.user.mapper;

import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

	UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

	GetUserResponse userToGetUserResponse(User user);

	User postUserRequestToUser(PostUserRequest postUserRequest);

}
