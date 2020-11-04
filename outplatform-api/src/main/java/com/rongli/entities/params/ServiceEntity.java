package com.rongli.entities.params;

import java.io.Serializable;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(description= "服务接口规则实体类")
@TableName("t_service")
public class ServiceEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "接口号")
	@TableId
	private String serviceId;
	
	@ApiModelProperty(value = "表名")
	private String tableName;
	
	@ApiModelProperty(value = "接口描述")
	private String serviceExplain;
	
	@ApiModelProperty(value = "数据类型")
	private String dataType;
	
	@ApiModelProperty(value = "输入参数规则")
	private String input;
	
	@ApiModelProperty(value = "输出参数规则")
	private String output;
	
	@ApiModelProperty(value = "是否存储数据库")
	private String savedb;
	
	public boolean isSaveDb() {
		if(savedb==null)
			return false;
		
		if(savedb.equals("1")) {
			return true;
		}else {
			return false;
		}
	}
	
}
