package top.duwd.common.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.duwd.common.domain.sub.entity.SubUser;
import top.duwd.common.exception.DuException;
import top.duwd.sub.service.SubUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


@Slf4j
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private SubUserService subUserService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        String requestPath = request.getRequestURI();
        log.debug("Method: " + method.getName() + ", Ignore: " + method.isAnnotationPresent(Ignore.class));
        log.debug("requestPath: " + requestPath);

        if (requestPath.contains("/error")) {
            return true;
        }
        if (method.isAnnotationPresent(Ignore.class)) {
            return true;
        }

        String token = request.getHeader("token");
        log.debug("token: " + token);
        if (StringUtils.isEmpty(token)) {
            throw new DuException(0,"无效token");
        }

        SubUser user = subUserService.getUserByToken(token);
        request.setAttribute("currentUser", user);
        return true;
    }
}
