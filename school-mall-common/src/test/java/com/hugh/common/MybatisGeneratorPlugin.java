package com.hugh.common;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author 52123
 * @since 2019/6/14 15:08
 */
public class MybatisGeneratorPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    /**
     * 在Model类上加上Lombok注解和作者信息
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addJavaDocLine("/**\n" +
                " * @author 52123\n" +
                " * @since " + getCurrentTime() + "\n" +
                " */");
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addImportedType("lombok.Data");
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     *  数据库注释不为空则添加字段注释
     */
    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (!StringUtils.isEmpty(introspectedColumn.getRemarks())) {
            field.addJavaDocLine("/**\n" +
                    "     * " + introspectedColumn.getRemarks() + "\n" +
                    "     */");
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    /**
     *  为Mapper添加注解
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.addAnnotation("@Mapper");
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        interfaze.addJavaDocLine("/**\n" +
                " * @author 52123\n" +
                " * @since " + getCurrentTime() + "\n" +
                " */");
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }


    /**
     *  去除getter
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    /**
     *  去除setter
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    /**
     * @return yyyy/MM/dd hh:mm
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy/MM/dd hh:mm")
                .format(new Date(System.currentTimeMillis()));
    }
}
