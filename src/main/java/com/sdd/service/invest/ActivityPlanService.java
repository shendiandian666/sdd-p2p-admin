package com.sdd.service.invest;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.sdd.dao.DaoSupport;
import com.sdd.model.PageData;
import com.sdd.util.Constants;
import com.sdd.util.Tools;

/** 
 * 说明： 
 * 创建人：kbky
 * 创建时间：
 * @version
 */
@Service("investActivityPlanService")
public class ActivityPlanService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	@Autowired
	private ActivityService activityService;
	
	/*
	 * 查询
	 */
	public List<Map<String, Object>> listService(int pageNum, int pageSize, PageData pd) throws Exception{
		PageHelper.startPage(pageNum, pageSize);
		Object object = dao.findForList("com.sdd.mapper.invest.ActivityPlanMapper.list", pd);
		return Tools.objToList(object);
	}
	
	public int hightPlanService(PageData pd) throws Exception {
		return (int) dao.findForObject("com.sdd.mapper.invest.ActivityPlanMapper.hightPlan", pd);
	}
	
	/*
	 * 保存
	 * 收益=本金*年利率/365*天数
	       年利率=收益/天数*365/本金*100%=20000/14*365/350000*100%=148.98%
	 */
	@Transactional
	public String saveService(PageData pd) throws Exception {
		String activityId = pd.getString("activity_id");
		float depositDisplay_s = Float.valueOf(pd.getString("depositDisplay_s"));//充值金额最少
		float depositDisplay_e = Float.valueOf(pd.getString("depositDisplay_e"));
		float rebate = Float.valueOf(pd.getString("rebate"));//返利比例
		float rebatePlus = Float.valueOf(pd.getString("rebatePlus"));//奖励比例
		int days = Integer.valueOf(pd.getString("days"));//方案天数
		float rate = Float.valueOf(pd.getString("rate"));//年化利率
		float redback = Float.valueOf(pd.getString("redback"));//红包
		float bbin = Float.valueOf(pd.getString("bbin"));//体验金
		//体验金收益(收益=本金*年利率/365*天数)
		float bbin_interest = bbin*rate/100/365*days;
		pd.put("bbin_interest", bbin_interest);
		float others = Float.valueOf(pd.getString("others"));//其它
		
		//计算最少总收益(返现+奖励+利息+红包+[体验金利息+其它])
		float totalIncome = depositDisplay_s*rebate/100 + depositDisplay_s*rebatePlus/100 + depositDisplay_s*rate/100/365*days + redback + bbin_interest + others;
		totalIncome = (float)(Math.ceil(totalIncome));
		//综合年化利率
		float yearRate = (float)(Math.ceil(totalIncome/days*365/depositDisplay_s*100*100))/100;
		pd.put("totalIncome", totalIncome);
		pd.put("year_rate", yearRate);
		
		//depositDisplay_e(为0时表示没有上限，1时表示只有开始一个固定金额)
		//充值金额描述
		String deposit_s = NumberFormat.getNumberInstance(Locale.CHINA).format(depositDisplay_s);
		String deposit_e = "";
		String totalIncome_txt = "";
		if(depositDisplay_e == 0){
			deposit_e = "无上限";
			totalIncome_txt = String.format("%.2f",totalIncome) + "↑";
		}else if(depositDisplay_e == 1){
			deposit_e = "";
			totalIncome_txt = String.format("%.2f",totalIncome) + "";
		}else{
			deposit_e = NumberFormat.getNumberInstance(Locale.CHINA).format(depositDisplay_e);
			float totalIncome_e = depositDisplay_e*rebate/100 + depositDisplay_e*rebatePlus/100 + depositDisplay_e*rate/100/365*days + redback + bbin_interest + others;
			totalIncome_e = (float)(Math.ceil(totalIncome_e*100))/100;
			totalIncome_txt = String.format("%.2f",totalIncome) + " - " + String.format("%.2f",totalIncome_e);
		}
		pd.put("totalIncome_txt", totalIncome_txt);//总收益范围
		if("".equals(deposit_e)){
			String depositDisplay_txt = deposit_s;
			pd.put("depositDisplay_txt", depositDisplay_txt);
		}else{
			String depositDisplay_txt = deposit_s + " - " + deposit_e;
			pd.put("depositDisplay_txt", depositDisplay_txt);
		}
		
		//保存方案
		int count = (int) dao.findForObject("com.sdd.mapper.invest.ActivityPlanMapper.isExists", pd);
		if(count > 0){
			return "主键ID已存在!";
		}else{
			dao.save("com.sdd.mapper.invest.ActivityPlanMapper.save", pd);
			//综合年化最高的设置为最佳方案
			int planCount = hightPlanService(pd);
			if(planCount < 1){
				PageData bestPd = new PageData();
				bestPd.put("bestName", pd.getString("name"));
				bestPd.put("bestDeposit", depositDisplay_s);
				bestPd.put("bestMoney", totalIncome);
				bestPd.put("bestInterest", yearRate);
				bestPd.put("ID", activityId);
				activityService.updateService(bestPd);
			}
			return Constants.SUCCESS;
		}
	}
	
	/*
	 * 修改页面数据查询
	 */
	public Map<String, Object> editService(PageData pd) throws Exception {
		Object object = dao.findForObject("com.sdd.mapper.invest.ActivityPlanMapper.listById", pd);
		return Tools.objToMap(object);
	}
	
	/*
	 * 修改
	 */
	@Transactional
	public String updateService(PageData pd) throws Exception {
		String activityId = pd.getString("activity_id");
		float depositDisplay_s = Float.valueOf(pd.getString("depositDisplay_s"));//充值金额最少
		float depositDisplay_e = Float.valueOf(pd.getString("depositDisplay_e"));
		float rebate = Float.valueOf(pd.getString("rebate"));//返利比例
		float rebatePlus = Float.valueOf(pd.getString("rebatePlus"));//奖励比例
		int days = Integer.valueOf(pd.getString("days"));//方案天数
		float rate = Float.valueOf(pd.getString("rate"));//年化利率
		float redback = Float.valueOf(pd.getString("redback"));//红包
		float bbin = Float.valueOf(pd.getString("bbin"));//体验金
		//体验金收益(收益=本金*年利率/365*天数)
		float bbin_interest = bbin*rate/100/365*days;
		pd.put("bbin_interest", bbin_interest);
		float others = Float.valueOf(pd.getString("others"));//其它
		
		//计算最少总收益(返现+奖励+利息+红包+[体验金利息+其它])
		float totalIncome = depositDisplay_s*rebate/100 + depositDisplay_s*rebatePlus/100 + depositDisplay_s*rate/100/365*days + redback + bbin_interest + others;
		totalIncome = (float)(Math.ceil(totalIncome));
		//综合年化利率
		float yearRate = (float)(Math.ceil(totalIncome/days*365/depositDisplay_s*100*100))/100;
		pd.put("totalIncome", totalIncome);
		pd.put("year_rate", yearRate);
		
		//depositDisplay_e(为0时表示没有上限，1时表示只有开始一个固定金额)
		//充值金额描述
		String deposit_s = NumberFormat.getNumberInstance(Locale.CHINA).format(depositDisplay_s);
		String deposit_e = "";
		String totalIncome_txt = "";
		if(depositDisplay_e == 0){
			deposit_e = "无上限";
			totalIncome_txt = String.format("%.2f",totalIncome) + "↑";
		}else if(depositDisplay_e == 1){
			deposit_e = "";
			totalIncome_txt = String.format("%.2f",totalIncome) + "";
		}else{
			deposit_e = NumberFormat.getNumberInstance(Locale.CHINA).format(depositDisplay_e);
			float totalIncome_e = depositDisplay_e*rebate/100 + depositDisplay_e*rebatePlus/100 + depositDisplay_e*rate/100/365*days + redback + bbin_interest + others;
			totalIncome_e = (float)(Math.ceil(totalIncome_e*100))/100;
			totalIncome_txt = String.format("%.2f",totalIncome) + " - " + String.format("%.2f",totalIncome_e);
		}
		pd.put("totalIncome_txt", totalIncome_txt);//总收益范围
		if("".equals(deposit_e)){
			String depositDisplay_txt = deposit_s;
			pd.put("depositDisplay_txt", depositDisplay_txt);
		}else{
			String depositDisplay_txt = deposit_s + " - " + deposit_e;
			pd.put("depositDisplay_txt", depositDisplay_txt);
		}
		
		int count = (int) dao.findForObject("com.sdd.mapper.invest.ActivityPlanMapper.isExists", pd);
		if(count > 0){
			dao.update("com.sdd.mapper.invest.ActivityPlanMapper.update", pd);
			//综合年化最高的设置为最佳方案
			int planCount = hightPlanService(pd);
			if(planCount < 1){
				PageData bestPd = new PageData();
				bestPd.put("bestName", pd.getString("name"));
				bestPd.put("bestDeposit", depositDisplay_s);
				bestPd.put("bestMoney", totalIncome);
				bestPd.put("bestInterest", yearRate);
				bestPd.put("ID", activityId);
				activityService.updateService(bestPd);
			}
			return Constants.SUCCESS;
		}else{
			return "记录已被其他用户删除!";		
		}
	}
	
	/*
	 * 删除
	 */
	public String delService(PageData pd) throws Exception {
		dao.delete("com.sdd.mapper.invest.ActivityPlanMapper.delete", pd);
		return Constants.SUCCESS;
	}
	
}

