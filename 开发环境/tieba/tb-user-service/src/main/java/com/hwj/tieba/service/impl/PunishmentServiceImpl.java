package com.hwj.tieba.service.impl;

import com.hwj.tieba.dao.PunishmentMapper;
import com.hwj.tieba.entity.Punishment;
import com.hwj.tieba.service.PunishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PunishmentServiceImpl implements PunishmentService {
    @Autowired
    private PunishmentMapper punishmentMapper;

    @Override
    public List<Punishment> queryPunishmentList(String userId, Integer[] stateIdArray, Date nowDate) {
        List<Punishment> punishmentList = punishmentMapper.queryPunishmentList(userId,stateIdArray,nowDate);
        return punishmentList;
    }

    @Override
    public List<Punishment> queryPunishmentAllList(String userId, Date nowDate) {
        List<Punishment> punishmentList = punishmentMapper.queryPunishmentAllList(userId,nowDate);
        return punishmentList;
    }
}
