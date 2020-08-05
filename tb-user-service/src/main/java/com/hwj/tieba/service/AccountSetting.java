package com.hwj.tieba.service;

import com.hwj.tieba.entity.OthersSetting;
import com.hwj.tieba.resp.ServerResponse;

public interface AccountSetting {
    /**
     * 保存对其他用户的设置
     * @param othersSetting
     * @param sessionId
     * @return
     */
    ServerResponse<String> addOthersUserSetting(OthersSetting othersSetting, String sessionId);

    /**
     * 获取对其他用户的设置
     * @param othersSetting
     * @param sessionId
     * @return
     */
    ServerResponse<OthersSetting> getOthersUserSetting(OthersSetting othersSetting, String sessionId);
}
