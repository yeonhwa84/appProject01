package com.visualnovel.repository;

import com.visualnovel.model.entity.VnUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VnUserMapper {
    VnUser selectById(String userId);
    VnUser selectByName(String userName);
    int    insertUser(VnUser user);
    int    updateLastLogin(String userId);
}
