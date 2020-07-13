package com.hwj.tieba.dao;

import com.hwj.tieba.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FileMapper {
    File queryImageById(@Param("fileId") String fileId);

    List<File> queryImageListById(@Param("idList") List<String> idList);
}
