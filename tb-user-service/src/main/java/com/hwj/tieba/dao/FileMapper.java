package com.hwj.tieba.dao;

import com.hwj.tieba.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FileMapper {
    File queryImageById(@Param("fileId") String fileId);
}
