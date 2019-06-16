package com.hugh.common;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author 52123
 * @since 2019/6/13 18:55
 */
public class MybatisGenerator {

    private static final String PATH = "D:\\project\\roncoocom-roncoo-education-master\\school-mall\\school-mall-seckill\\src\\main";
    private static final String targetProject = PATH + "\\java";
    private static final String targetResource = PATH + "\\resources";
    private static final String targetPackage = "com.hugh.order";

    public static void main(String[] args) throws Exception {
        List<String> warnings = new ArrayList<>();
        Configuration config = new Configuration();
        Context context = new Context(ModelType.CONDITIONAL);
        context.setId("generatorContext");

        // 自定义插件
        PluginConfiguration pluginConfig = new PluginConfiguration();
        pluginConfig.setConfigurationType("com.hugh.common.MybatisGeneratorPlugin");
        context.addPluginConfiguration(pluginConfig);

        // JDBC connection
        JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
        jdbcConfig.setConnectionURL("jdbc:mysql://localhost:3306/school_mall?serverTimezone=UTC&characterEncoding=utf-8&useSSL=false");
        jdbcConfig.setDriverClass("com.mysql.cj.jdbc.Driver");
        jdbcConfig.setUserId("root");
        jdbcConfig.setPassword("123456");
        // 去除不同数据库相同表名的情况
        jdbcConfig.addProperty("nullCatalogMeansCurrent","true");
        context.setJdbcConnectionConfiguration(jdbcConfig);

        JavaClientGeneratorConfiguration clientConfig = new JavaClientGeneratorConfiguration();
        clientConfig.setTargetProject(targetProject);
        clientConfig.setTargetPackage(targetPackage + ".mapper");
        clientConfig.addProperty("enableSubPackages","true");
        clientConfig.setConfigurationType("XMLMAPPER");
        context.setJavaClientGeneratorConfiguration(clientConfig);


        // 指定生成 Java 模型对象所属的包
        JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();
        // 目录相对路径
        modelConfig.setTargetProject(targetProject);
        // 对应的包名
        modelConfig.setTargetPackage(targetPackage + ".entity");
        modelConfig.addProperty("enableSubPackages","false");
        context.setJavaModelGeneratorConfiguration(modelConfig);


        // 生成的SQL映射文件
        SqlMapGeneratorConfiguration sqlConfig = new SqlMapGeneratorConfiguration();
        sqlConfig.setTargetPackage("mapper");
        sqlConfig.setTargetProject(targetResource);
        sqlConfig.addProperty("enableSubPackages","false");
        context.setSqlMapGeneratorConfiguration(sqlConfig);


        // 去除注释自动生成
        CommentGeneratorConfiguration commentConfig = new CommentGeneratorConfiguration();
//        commentConfig.setConfigurationType("com.hugh.common.MybatisGeneratorComment");
        commentConfig.addProperty("suppressAllComments","true");
        context.setCommentGeneratorConfiguration(commentConfig);

        // 指定表名
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入表名：");
        TableConfiguration tableConfig = new TableConfiguration(context);
        tableConfig.setTableName(sc.nextLine());
        tableConfig.addProperty("enableSelectByExample","false");
        tableConfig.setSelectByExampleStatementEnabled(false);
        tableConfig.setCountByExampleStatementEnabled(false);
        tableConfig.setDeleteByExampleStatementEnabled(false);
        tableConfig.setUpdateByExampleStatementEnabled(false);
        context.addTableConfiguration(tableConfig);

        config.addContext(context);
        DefaultShellCallback callback = new DefaultShellCallback(true);

        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);

    }
}
