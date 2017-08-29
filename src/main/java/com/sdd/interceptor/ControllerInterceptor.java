package com.sdd.interceptor;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sdd.model.system.User;

@Aspect
@Component
public class ControllerInterceptor {

	private Logger logger = LogManager.getLogger(getClass());

	/**
	 * 定义拦截规则
	 */
	@Pointcut("execution(* com.zyx.web..*(..)) and @annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void controllerMethodPointcut() {
	}

	@Before("controllerMethodPointcut()")
	public void doBefore(JoinPoint joinPoint) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();

		logger.info("URL:" + request.getRequestURL().toString());
		logger.info("HTTP_METHOD:" + request.getMethod());
		logger.info("IP:" + request.getRemoteAddr());
		logger.info("CLASS_METHOD:" + joinPoint.getSignature().getDeclaringTypeName() + "."
				+ joinPoint.getSignature().getName());
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		//logger.info("USER:" + user.getUserName());
	}

}
