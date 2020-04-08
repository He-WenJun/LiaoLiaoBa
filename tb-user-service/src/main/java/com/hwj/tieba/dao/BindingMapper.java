package com.hwj.tieba.dao;

import com.hwj.tieba.entity.Binding;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface BindingMapper {
    /**
     * 插入一条绑定记录
     * @param binding 绑定实例
     * @return 受影响的行数
     */
    public int insertBinding(Binding binding);
}
