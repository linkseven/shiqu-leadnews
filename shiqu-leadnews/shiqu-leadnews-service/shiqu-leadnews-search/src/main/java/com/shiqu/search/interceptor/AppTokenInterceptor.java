package com.shiqu.search.interceptor;

import com.shiqu.model.user.pojos.ApUser;
import com.shiqu.utils.thread.AppThreadLocalUtil;
import com.shiqu.utils.thread.UserThreadUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        if(userId != null){
            //存入到当前线程中
            ApUser apUser = new ApUser();
            apUser.setId(Integer.valueOf(userId));
            AppThreadLocalUtil.setUser(apUser);
            UserThreadUtil.setId(Integer.valueOf(userId));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Integer id = UserThreadUtil.getId();
        System.out.println(id);
        AppThreadLocalUtil.clear();
    }
}
