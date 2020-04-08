package com.hwj.tieba.config;

import com.hwj.tieba.filter.post.LoginTokenFiltre;
import com.hwj.tieba.filter.post.SessionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public SessionFilter getSessionFilter(){
       return new SessionFilter();
    }
    @Bean
    public LoginTokenFiltre getLoginTokenFiltre(){
        return new LoginTokenFiltre();
    }
}
