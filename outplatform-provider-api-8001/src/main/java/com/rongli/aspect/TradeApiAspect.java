package com.rongli.aspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.rongli.common.utils.DateUtil;
import com.rongli.common.utils.SpringUtil;
import com.rongli.entities.params.ServiceEntity;
import com.rongli.entities.params.ServiceResult;
import com.rongli.exception.BaseException;
import com.rongli.mapper.DbMapper;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class TradeApiAspect {

	private final String executeExpr = "execution(* com.rongli.service.TradeService.doTrade(..))";
	  
	@Before(executeExpr) //在切入点的方法run之前要干的
    public void logBeforeController(JoinPoint pjd) {
        
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			HttpServletRequest request = requestAttributes.getRequest();

			log.info("===============请求开始===============");
			log.info("请求地址:" + request.getRequestURL().toString());
			log.info("请求方式:" + request.getMethod());
			log.info("请求类方法:" + pjd.getSignature());
			log.info("请求类方法参数:" + Arrays.toString(pjd.getArgs()));

			ServiceEntity se= (ServiceEntity) Arrays.asList(pjd.getArgs()).get(0);
			if(!se.isValid())
				throw new BaseException("当前"+se.getServiceId()+"不允许交易");
    }

	  
	@AfterReturning(returning = "ret", pointcut = executeExpr)
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
		ServiceEntity se= (ServiceEntity) Arrays.asList(joinPoint.getArgs()).get(0);
		if(ret!=null) {
			ServiceResult sr=(ServiceResult) ret;
			
			log.info("返回内容:"+sr.getToThirdData()); 
			if(se.isSaveDb()) {
				saveDb(se,sr);
			}
			System.out.println(sr.getDbData());
			log.info("===============请求结束===============");
		}
    }
	
	public void saveDb(ServiceEntity se,ServiceResult sr) {
		try {
			List<String> keys=new ArrayList<String>();
			List<String> values=new ArrayList<String>();
			
			keys.add("trade_result");
			keys.add("trade_message");
			keys.add("trade_time");
			if(sr.getResult()) {//保存成功的结果
				values.add("0");
			}else {
				values.add("1");
			}
			values.add(sr.getMsg());
			values.add(DateUtil.getTodayToSecond());
			for(String key:sr.getDbData().keySet()){
				keys.add(key);
				values.add(sr.getDbData().getString(key));
			}
			DbMapper dbMapper=SpringUtil.getBean(DbMapper.class);
			int row =dbMapper.saveDB(se.getTableName(), keys, values);
			if(row==1)
				log.info("插入成功");
		}catch (Exception e) {
			// TODO: handle exception
			log.error("存储db失败:"+e.getMessage());
		}
	}
	  
  @AfterThrowing(pointcut = executeExpr, throwing = "ex")
  public void doAfterThrowing(JoinPoint joinPoint, Exception ex) {
      String methodName = joinPoint.getSignature().getName();
      List<Object> args = Arrays.asList(joinPoint.getArgs());
      log.error("连接点方法为：" + methodName + ",参数为：" + args + ",异常为：" + ex);
      log.info("===============请求结束===============");
        
  }
}
