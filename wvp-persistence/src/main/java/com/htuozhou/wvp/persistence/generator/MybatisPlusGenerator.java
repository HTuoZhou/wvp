package com.htuozhou.wvp.persistence.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Collections;

/**
 * @author hanzai
 * @date 2023/2/2
 */
public class MybatisPlusGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/wvpDb?serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=utf8", "root", "123456")
                .globalConfig(builder -> builder.author("HTuoZhou")
                        // .enableSwagger()
                        // .fileOverride()
                        .outputDir(System.getProperty("user.dir") + "/mybatis-plus-generator" + "/src/main/java")
                        .disableOpenDir())
                .packageConfig(builder -> builder.parent("com.htuozhou.wvp.persistence")
                        // .moduleName("")
                        .pathInfo(Collections.singletonMap(OutputFile.mapperXml, System.getProperty("user.dir") + "/mybatis-plus-generator" + "/src/main/resources/mapper")))
                .strategyConfig(builder -> builder.addInclude("user")
                        .entityBuilder()
                        .formatFileName("%sPO")
                        .enableLombok()
                        .enableChainModel()
                        .idType(IdType.AUTO)
                        .enableTableFieldAnnotation()
                        .addTableFills(new Column("create_time", FieldFill.INSERT))
                        .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE))
                        .logicDeleteColumnName("deleted")
                        .versionColumnName("version")
                        .controllerBuilder()
                        .enableRestStyle()
                        .enableHyphenStyle())// 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

}
