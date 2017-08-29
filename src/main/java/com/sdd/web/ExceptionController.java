package com.sdd.web;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ExceptionController extends BasicErrorController {

	public ExceptionController() {
		super(new DefaultErrorAttributes(), new ErrorProperties());
	}

	@RequestMapping
	@ResponseBody
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		//HttpStatus status = getStatus(request);
		return super.error(request);
	}

	@RequestMapping(produces = {"text/html"})
	public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
		//return super.errorHtml(request, response);
		HttpStatus status = getStatus(request);
		if(status != HttpStatus.NOT_FOUND){
			return new ModelAndView("redirect:/index");
		}
		System.out.println("text/html status:"+status);
		return new ModelAndView("/404", this.getErrorAttributes(request, false));
	}

	
	
	
}
