package com.yoshisu.mybatis.generator.plugin;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;

public class OptionalPlugin extends PluginAdapter {

    private static final String PROPERTY_OPTIONAL_METHOD_PREFIX = "optionalMethodPrefix";

    private String optionalMethodPrefix;

    @Override
    public boolean validate(List<String> warnings) {
        optionalMethodPrefix = properties.getProperty(PROPERTY_OPTIONAL_METHOD_PREFIX, "selectOptional");
        return true;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze,
            IntrospectedTable introspectedTable) {
        makeMethodReturnOptional(method, interfaze, introspectedTable);
        return true;
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze,
            IntrospectedTable introspectedTable) {
        if (!isListReturnType(method, interfaze)) {
            makeMethodReturnOptional(method, interfaze, introspectedTable);
        }
        return true;
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
            IntrospectedTable introspectedTable) {
        if (!isListReturnType(method, interfaze)) {
            makeMethodReturnOptional(method, interfaze, introspectedTable);
        }
        return true;
    }

    private void makeMethodReturnOptional(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType oldType = method.getReturnType().orElseGet(interfaze::getType);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType("Optional<" + oldType.getFullyQualifiedName() + ">");

        Method newMethod = new Method(method);
        newMethod.setReturnType(type);
        newMethod.setName(optionalMethodPrefix + method.getName().substring("select".length()));

        interfaze.addMethod(newMethod);
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.Optional"));
        interfaze.addImportedType(oldType);

        context.getCommentGenerator().addGeneralMethodComment(newMethod, introspectedTable);
    }

    private boolean isListReturnType(Method method, Interface interfaze) {
        return method.getReturnType().orElseGet(interfaze::getType).getShortName().startsWith("List");
    }
}
