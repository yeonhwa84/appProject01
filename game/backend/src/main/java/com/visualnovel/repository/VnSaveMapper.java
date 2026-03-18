package com.visualnovel.repository;

import com.visualnovel.model.entity.VnSave;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VnSaveMapper {
    VnSave  selectByUserId(String userId);
    boolean existsByUserId(String userId);
    int     upsertSave(VnSave save);
    int     deleteByUserId(String userId);
}
