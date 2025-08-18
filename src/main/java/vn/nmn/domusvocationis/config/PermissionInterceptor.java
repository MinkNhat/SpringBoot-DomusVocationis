package vn.nmn.domusvocationis.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import vn.nmn.domusvocationis.domain.Permission;
import vn.nmn.domusvocationis.domain.User;
import vn.nmn.domusvocationis.service.UserService;
import vn.nmn.domusvocationis.util.SecurityUtil;
import vn.nmn.domusvocationis.util.error.PermissionException;

import java.util.List;

@Transactional(readOnly = true)
public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        if(!email.isEmpty()) {
            boolean hasPermission = userService.checkUserPermission(email, path, httpMethod);
            if(!hasPermission) {
                throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
            }
        }

        return true;
    }
}

