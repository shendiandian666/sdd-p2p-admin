package com.sdd.web.system;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.sdd.service.system.LoginService;
import com.sdd.util.Constants;
import com.sdd.web.WebController;

@Controller
@RequestMapping("/system")
public class LoginController extends WebController {

	private Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	private LoginService loginService;
	
	/**
	 * 登录验证
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping("/login")
	public String login(HttpServletRequest request, Map<String, Object> map) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		Subject currentUser = SecurityUtils.getSubject();
		try {
			//调用login方法后会执行自定义MyShiroRealm中的doGetAuthenticationInfo方法进行验证
			currentUser.login(token);
		} catch (UnknownAccountException uae) {
			logger.warn("UnknownAccountException", uae);
		} catch (IncorrectCredentialsException ice) {
			logger.warn("密码不正确", ice);
		} catch (LockedAccountException lae) {
			logger.warn("账户已锁定", lae);
		} catch (ExcessiveAttemptsException eae) {
			logger.warn("用户名或密码错误次数过多", eae);
		} catch (AuthenticationException ae) {
			logger.warn("用户名或密码不正确", ae);
		} catch(Exception e){
			logger.warn("其它异常", e);
		}
		if(currentUser.isAuthenticated()){
			return "redirect:/system/main";
		}else{
			token.clear();
			map.put("error", "用户名或密码不正确");
			return "/system/index/login";
		}
		
	}

	/**
	 * 退出登录
	 * @param map
	 * @return
	 */
	@RequestMapping("/logout")
	public String logout(Map<String, Object> map) {
		SecurityUtils.getSubject().logout();
		map.put("SYSNAME", Constants.SYSNAME);
		return "/system/index/login";
	}

	/**
	 * 进入首页后的默认页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/login_default")
	public String defaultPage(Map<String, Object> map) throws Exception {
		map.put("SYSNAME", Constants.SYSNAME);
		return "/system/index/default";
	}

	@RequestMapping("/tologin")
	public String tologin(Map<String, Object> map) {
		map.put("SYSNAME", Constants.SYSNAME);
		return "/system/index/login";
	}
	
	@RequiresPermissions("login")//让MyShiroRealm中的doGetAuthorizationInfo方法执行
	@RequestMapping("/main")
	public String mainHtml(HttpServletRequest request, Map<String, Object> map) throws Exception {
		StringBuilder treeSb = loginService.mainService(request);
		map.put("menuList", treeSb);
		map.put("SYSNAME", Constants.SYSNAME);
		return "/system/index/main";
	}
	
	/**
	 * 进入tab标签
	 * 
	 * @return
	 */
	@RequestMapping("/tab")
	public String tab() {
		return "/system/index/tab";
	}

}
