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
                "/api/v1/open-periods",
                "/api/v1/payments/vn-pay-ipn",

                "/api/v1/answers",
                "/api/v1/questions",
                "/api/v1/questions/**",

                "/api/v1/fee-registers/**",
                "/api/v1/users/{id}/fee-registers",
                "/api/v1/users/{id}/payments",
                "/api/v1/users/{id}/sessions",

                "/api/v1/users/change-password/**",
                "/api/v1/payments/vn-pay",


                // tạm kh check
                // "/api/v1/fee-registers",



                // giới hạn method trong security config
                "/api/v1/sessions/**",
                "/api/v1/periods/**",

                "/api/v1/categories",
                "/api/v1/posts",
                "/api/v1/posts/**",

                "/api/v1/users/{id}",
                "/api/v1/fee-types",
        };
        registry.addInterceptor(getPermissionInterceptor()).excludePathPatterns(whiteList);
    }
}

