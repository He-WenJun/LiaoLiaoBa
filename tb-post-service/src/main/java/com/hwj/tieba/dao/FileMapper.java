package com.hwj.tieba.dao;

import com.hwj.tieba.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FileMapper {
    /**
     * 查询单个文件
     * @param fileId 文件Id
     * @return 文件实例
     */
    File queryFileById(@Param("fileId") String fileId);

    /**
     * 查询多个文件
     * @param idList 文件集合
     * @return 文件实例集合
     */
    List<File> queryFileListById(@Param("idList") List<String> idList);

    /**
     * 插入多个文件
     * @param fileList 文件实例集合
     * @return 受影响行数
     */
    Integer insertFileList(@Param("fileList") List<File> fileList);

    /**
     * 插入单个文件
     * @param file 文件实例
     * @return 受影响行数
     */
    Integer insertFile(@Param("file") File file);
}
