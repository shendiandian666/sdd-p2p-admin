package com.sdd.web;

import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import com.sdd.model.PageData;
import com.sdd.model.system.User;

public class WebController {

	/** new PageData对象
	 * @return
	 */
	public PageData getPageData(){
		PageData pd = new PageData(this.getRequest());
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		pd.put("user", user);
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}else{
			pd.put("keywords", "");
		}
		return pd;
	}
	
	public HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}
	
	public ModelAndView getModelAndView(){
		return new ModelAndView();
	}
	
}
