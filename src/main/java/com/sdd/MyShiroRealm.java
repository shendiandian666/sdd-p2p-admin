package com.sdd;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.sdd.model.PageData;
import com.sdd.model.system.User;
import com.sdd.service.system.MenuService;
import com.sdd.service.system.RoleService;
import com.sdd.service.system.UserService;
import com.sdd.util.RightsHelper;
import com.sdd.util.Tools;

public class MyShiroRealm extends AuthorizingRealm {

	@Resource
	private UserService userService;
	
	@Resource
	private MenuService menuService;
	
	@Resource
	private RoleService roleService;
	
	/**
     * 权限认证，为当前登录的Subject授予角色和权限 
     * @see 经测试：本例中该方法的调用时机为需授权资源被访问时 
     * @see 经测试：并且每次访问需授权资源时都会执行该方法中的逻辑，这表明本例中默认并未启用AuthorizationCache 
     * @see 经测试：如果连续访问同一个URL（比如刷新），该方法不会被重复调用，Shiro有一个时间间隔（也就是cache时间，在ehcache-shiro.xml中配置），超过这个时间间隔再刷新页面，该方法会被执行
     */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");  
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();  
        User user = (User) principals.getPrimaryPrincipal();
        String userRights = user.getRights();
        String roleRights = user.getRoleRights();
        userRights = "".equals(userRights) || null == userRights ? "0" : userRights;
        roleRights = "".equals(roleRights) || null == roleRights ? "0" : roleRights;
        
        BigInteger userRoleRights = new BigInteger("0");
        try {
        	PageData pd = new PageData();
        	pd.put("user", user);
			List<Map<String, Object>> roleList = roleService.listAllService(pd);
			for(Map<String, Object> m : roleList){
				String roleId = Tools.getStringValue(m.get("ROLE_ID"));
				if(RightsHelper.testRights(roleRights, roleId)){
					String roleRight = Tools.getStringValue(m.get("ROLE_RIGHTS"));
					roleRight = "".equals(roleRight) ? "0" : roleRight;
					userRoleRights = userRoleRights.or(new BigInteger(roleRight));
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        
        BigInteger rights = new BigInteger(userRights).or(userRoleRights);
        try {
        	List<Map<String, Object>> menus = menuService.getAllMenus();
        	for(Map<String, Object> m : menus){
        		String menuId = m.get("MENU_ID").toString();
        		boolean b = RightsHelper.testRights(rights.toString(), menuId);
        		if(b){
        			String menuUrl = m.get("MENU_URL").toString(); 
        			authorizationInfo.addStringPermission(menuId+menuUrl);
        			authorizationInfo.addStringPermission(menuUrl);
        		}
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        authorizationInfo.addStringPermission("login");
        return authorizationInfo;  
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		System.out.println("MyShiroRealm.doGetAuthenticationInfo()");  
        // 获取用户的输入帐号  
		UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = (String) token.getUsername(); 
        if(username == null || "".equals(username)){
        	return null;
        }
        // 实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法  
        User userInfo = null;
		try {
			userInfo = userService.findByName(username);
		} catch (Exception e) {
			e.printStackTrace();
		}  
        if (userInfo == null) {  
            return null;  
        }
        String passwd = userInfo.getPassword();
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userInfo, // 用户名  
        		passwd, // 密码  
                getName() // realm name  
        );
        return authenticationInfo;  
	}

}
