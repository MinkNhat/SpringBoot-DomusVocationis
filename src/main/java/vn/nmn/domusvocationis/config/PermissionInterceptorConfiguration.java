package vn.nmn.domusvocationis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/", "/api/v1/auth/**", "/storage/**",
                "/api/v1/files",

                //tam thoi kh check permis
//                "/**",
                "/api/v1/periods/**",
                "/api/v1/periods",
                "/api/v1/slots/**",
                "/api/v1/slots",
                "/api/v1/users/bulk-create",
        };
        registry.addInterceptor(getPermissionInterceptor()).excludePathPatterns(whiteList);
    }
}

