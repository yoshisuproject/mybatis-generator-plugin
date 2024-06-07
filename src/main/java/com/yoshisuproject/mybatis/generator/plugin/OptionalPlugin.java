package com.yoshisuproject.mybatis.generator.plugin;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;

/**
 * A plugin to wrap MyBatis Generator's select methods with Optional return type.
 * This plugin will create additional methods that return Optional versions of the
 * select method results.
 * <p>
 * For example, if there is a method "selectByPrimaryKey",
 * this plugin will create "selectOptionalByPrimaryKey" that returns an Optional
 * of the same type.
 */
public class OptionalPlugin extends PluginAdapter {

    private static final String PROPERTY_OPTIONAL_METHOD_PREFIX = "optionalMethodPrefix";

    private String optionalMethodPrefix;

    /**
     * Validates the plugin configuration. This method will read the optionalMethodPrefix
     * property from the plugin configuration, defaulting to "selectOptional" if not specified.
     *
     * @param warnings The list of warnings to which problems should be added
     * @return always true as this plugin is always valid
     */
    @Override
    public boolean validate(List<String> warnings) {
        optionalMethodPrefix = properties.getProperty(PROPERTY_OPTIONAL_METHOD_PREFIX, "selectOptional");
        return true;
    }

    /**
     * Processes the selectByPrimaryKey method to create an Optional version.
     * The new method will have the same parameters but return an Optional of the
     * original return type.
     *
     * @param method The selectByPrimaryKey method
     * @param interfaze The mapper interface
     * @param introspectedTable The introspected table information
     * @return always true to indicate the method should be generated
     */
    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(
            Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        makeMethodReturnOptional(method, interfaze, introspectedTable);
        return true;
    }

    /**
     * Processes the selectByExampleWithBLOBs method to create an Optional version
     * if the return type is not a List. The new method will have the same parameters
     * but return an Optional of the original return type.
     *
     * @param method The selectByExampleWithBLOBs method
     * @param interfaze The mapper interface
     * @param introspectedTable The introspected table information
     * @return always true to indicate the method should be generated
     */
    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(
            Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        if (!isListReturnType(method, interfaze)) {
            makeMethodReturnOptional(method, interfaze, introspectedTable);
        }
        return true;
    }

    /**
     * Processes the selectByExampleWithoutBLOBs method to create an Optional version
     * if the return type is not a List. The new method will have the same parameters
     * but return an Optional of the original return type.
     *
     * @param method The selectByExampleWithoutBLOBs method
     * @param interfaze The mapper interface
     * @param introspectedTable The introspected table information
     * @return always true to indicate the method should be generated
     */
    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(
            Method method, Interface interfaze, IntrospectedTable introspectedTable) {
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
        return method.getReturnType()
                .orElseGet(interfaze::getType)
                .getShortName()
                .startsWith("List");
    }
}
