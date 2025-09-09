package knou.cbt.domain.user.mapper;

import knou.cbt.domain.user.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByEmail(String email);
}