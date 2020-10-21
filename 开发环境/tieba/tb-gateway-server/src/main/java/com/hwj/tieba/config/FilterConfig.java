package com.hwj.tieba.config;

import com.hwj.tieba.filter.post.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public SessionFilter getSessionFilter(){
       return new SessionFilter();
    }
    @Bean
    public LoginTokenFilter getLoginTokenFilter(){
        return new LoginTokenFilter();
    }
    @Bean
    public ModuleAdminFilter getModuleAdminFilter(){
        return new ModuleAdminFilter();
    }
    @Bean
    public ModuleBlackUserFilter getModuleBlackUserFilter(){
        return new ModuleBlackUserFilter();
    }
    @Bean
    public UserOthersSettingFilter getUserOthersSettingFilter(){
        return new UserOthersSettingFilter();
    }

}
