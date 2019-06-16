package com.hugh.common;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

/**
 * @author 52123
 * @since 2019/6/13 19:18
 */
public class MybatisGeneratorComment implements CommentGenerator {

    @Override
    public void addConfigurationProperties(Properties properties) {}

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {


    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {}

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass,
                                     IntrospectedTable introspectedTable) {

    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        innerClass.addJavaDocLine("/**\n" +
                " * @author 52123\n" +
                " * @since " + getCurrentTime() + "\n" +
                " */");
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {}

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {}

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {}

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {}

    @Override
    public void addComment(XmlElement xmlElement) {}

    @Override
    public void addRootComment(XmlElement rootElement) {}

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    /**
     * @return yyyy/MM/dd hh:mm
     */
    private String getCurrentTime(){
        return new SimpleDateFormat("yyyy/MM/dd hh:mm")
                .format(new Date(System.currentTimeMillis()));
    }
}
