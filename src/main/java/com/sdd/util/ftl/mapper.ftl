<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zyx.mapper.${model}.${class}Mapper">

	<!--表名 -->
	<sql id="tableName">
		"${tableName}"
	</sql>
	
	<!-- 字段 -->
	<sql id="Field">
		<#list columnMap?keys as key>  
		${key},
		</#list>
		${primaryKey}
	</sql>
	
	<!-- 字段值 -->
	<sql id="FieldValue">
		<#list columnMap?keys as key>  
		${r"#{"}${key}${r"}"},
		</#list>
		${sequence}		
	</sql>
	
	<select id="list" parameterType="java.util.Map" resultType="java.util.Map">
		select 
		<#list columnMap?keys as key>  
			<#if columnMap[key][1] == 'true'>
				${key},
				(select name
		          from jt_dictionaries
		         where parent_id in
		               (select dic_id
		                  from jt_column_dic dic
		                 where dic.table_name = '${tableName}'
		                   and dic.table_column = '${key}')
		           and bianma = ${key}) ${key}_DESC,
			<#else>
				${key},
			</#if>
		</#list>
		${primaryKey}
		from 
		<#if useOrgan == 'true'>
			<include refid="tableName"></include> a,(select * from jt_organ start with organ_id= ${r"#{user.organId}"} connect by prior organ_id = parent_id) b
			where a.oper_id = b.organ_id
		<#else>
			<include refid="tableName"></include>
			where 1=1 
		</#if>
		
		
	</select>
	
	<select id="isExists" parameterType="java.util.Map" resultType="java.lang.Integer">
		select count(1) from 
		<include refid="tableName"></include> 
		where ${primaryKey} = ${r"#{"}${primaryKey}${r"}"}
	</select>
	
	<select id="listById" parameterType="java.util.Map" resultType="java.util.Map">
		select 
		<#list columnMap?keys as key>  
			<#if columnMap[key][1] == 'true'>
				${key},
				(select name
		          from jt_dictionaries
		         where parent_id in
		               (select dic_id
		                  from jt_column_dic dic
		                 where dic.table_name = '${tableName}'
		                   and dic.table_column = '${key}')
		           and bianma = ${key}) ${key}_DESC,
			<#else>
				${key},
			</#if>
		</#list>
		${primaryKey}
		from 
		<include refid="tableName"></include>
		where ${primaryKey} = ${r"#{"}${primaryKey}${r"}"}
	</select>
	
	<insert id="save" parameterType="java.util.Map">
		insert into 
		<include refid="tableName"></include>
		(
		<include refid="Field"></include>
		) values (
		<include refid="FieldValue"></include>
		)
	</insert>
	
	<update id="update" parameterType="java.util.Map">
		update 
		<include refid="tableName"></include>
		set 
		<#list columnMap?keys as key>  
		${key} = ${r"#{"}${key}${r"}"},
		</#list>
		${primaryKey} = ${primaryKey}
		where ${primaryKey} = ${r"#{"}${primaryKey}${r"}"}
	</update>
	
	<delete id="delete" parameterType="java.util.Map">
		delete from 
		<include refid="tableName"></include>
		where ${primaryKey} = ${r"#{"}${primaryKey}${r"}"}
	</delete>
    
</mapper>