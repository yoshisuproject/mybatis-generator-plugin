package com.yoshisuproject.mybatis.generator.plugin;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * MyBatis Generator plugin to generate toString() method without serialVersionUID field.
 * <p>
 * Configuration properties:
 * <ul>
 *   <li>useToStringFromRoot: Whether to include super.toString() result (true/false)</li>
 *   <li>sign: Bracket style to use (PAREN/BRACKET/BRACE/THAN_SIGN)</li>
 *   <li>openSign: Direct specification of opening bracket ((, [, {, <)</li>
 * </ul>
 */
public class ToStringWithoutSerialVersionUidPlugin extends PluginAdapter {

    private enum Sign {
        PAREN('(', ')'),
        BRACKET('[', ']'),
        BRACE('{', '}'),
        THAN_SIGN('<', '>');

        private final char open;

        private final char close;

        Sign(char open, char close) {
            this.open = open;
            this.close = close;
        }

        public char getOpen() {
            return open;
        }

        public char getClose() {
            return close;
        }

        public static Sign getDefault() {
            return PAREN;
        }

        public static Optional<Sign> getByOpenSign(String open) {
            for (Sign sign : Sign.values()) {
                if (String.valueOf(sign.getOpen()).equals(open)) {
                    return Optional.of(sign);
                }
            }
            return Optional.empty();
        }
    }

    private boolean useToStringFromRoot;

    private Sign sign;

    private final FullyQualifiedJavaType serializable;

    private final Field serialVersionUID;

    public ToStringWithoutSerialVersionUidPlugin() {
        super();
        this.serializable = new FullyQualifiedJavaType("java.io.Serializable");
        this.serialVersionUID = new Field("serialVersionUID", new FullyQualifiedJavaType("long"));
    }

    /**
     * Sets plugin properties.
     *
     * @param properties The properties configured for this plugin
     */
    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        this.useToStringFromRoot = StringUtility.isTrue(properties.getProperty("useToStringFromRoot"));
    }

    /**
     * Validates the plugin configuration.
     * Checks for valid sign configuration and sets default if invalid.
     *
     * @param warnings Warnings during validation will be added to this list
     * @return always true as this plugin is always valid
     */
    @Override
    public boolean validate(List<String> warnings) {
        if (StringUtility.stringHasValue(properties.getProperty("openSign"))) {
            Optional<Sign> signOpt = Sign.getByOpenSign(properties.getProperty("openSign"));
            if (signOpt.isPresent()) {
                this.sign = signOpt.get();
            } else {
                this.sign = Sign.getDefault();
                warnings.add(MessageFormat.format(
                        "Plugin ToStringWithoutSerivalVersionUidPlugin only support with property name for openSign and value is `(`, `[`, `'{'`, `<`. Used defalut {0} sign",
                        Sign.getDefault()));
            }
        } else {
            if (StringUtility.stringHasValue(properties.getProperty("sign"))) {
                String signValue = properties.getProperty("sign");
                try {
                    this.sign = Sign.valueOf(signValue);
                } catch (IllegalArgumentException e) {
                    this.sign = Sign.getDefault();
                    warnings.add(MessageFormat.format(
                            "Plugin ToStringWithoutSerivalVersionUidPlugin only support value {0} with property sign. Used default {1} sign",
                            signValue, Sign.getDefault()));
                }
            } else {
                this.sign = Sign.getDefault();
                warnings.add(MessageFormat.format(
                        "Plugin ToStringWithoutSerivalVersionUidPlugin only support with property name for openSign and sign. Used default {0} sign",
                        Sign.getDefault()));
            }
        }

        return true;
    }

    /**
     * Generates toString method for base record class.
     *
     * @param topLevelClass The class being generated
     * @param introspectedTable The table introspected
     * @return always true
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    /**
     * Generates toString method for record with BLOBs class.
     *
     * @param topLevelClass The class being generated
     * @param introspectedTable The table introspected
     * @return always true
     */
    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    /**
     * Generates toString method for primary key class.
     *
     * @param topLevelClass The class being generated
     * @param introspectedTable The table introspected
     * @return always true
     */
    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    private void generateToString(IntrospectedTable introspectedTable, TopLevelClass topLevelClass) {
        Method method = new Method("toString");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.addAnnotation("@Override");

        if (introspectedTable.getTargetRuntime() == IntrospectedTable.TargetRuntime.MYBATIS3_DSQL) {
            context.getCommentGenerator()
                    .addGeneralMethodAnnotation(method, introspectedTable, topLevelClass.getImportedTypes());
        } else {
            context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        }

        boolean hasSerializable = topLevelClass.getSuperInterfaceTypes().contains(serializable);

        method.addBodyLine("StringBuilder sb = new StringBuilder();");
        method.addBodyLine("sb.append(getClass().getSimpleName());");
        method.addBodyLine("sb.append(\"" + sign.getOpen() + "\");");

        StringBuilder sb = new StringBuilder();
        AtomicBoolean isFirst = new AtomicBoolean(true);

        topLevelClass.getFields().stream()
                .filter(field -> !hasSerializable || !isSerialVersionUID(field))
                .forEach(field -> {
                    String fieldName = field.getName();
                    sb.setLength(0);
                    sb.append("sb.append(\"")
                            .append(isFirst.getAndSet(false) ? "" : ", ")
                            .append(fieldName)
                            .append("=\")")
                            .append(".append(")
                            .append(fieldName)
                            .append(");");
                    method.addBodyLine(sb.toString());
                });

        method.addBodyLine("sb.append(\"" + sign.getClose() + "\");");
        if (useToStringFromRoot && topLevelClass.getSuperClass().isPresent()) {
            method.addBodyLine("sb.append(\", from super class \");");
            method.addBodyLine("sb.append(super.toString());");
        }
        method.addBodyLine("return sb.toString();");

        topLevelClass.addMethod(method);
    }

    private boolean isSerialVersionUID(Field field) {
        return serialVersionUID.getName().equals(field.getName())
                && serialVersionUID.getType().equals(field.getType())
                && field.getVisibility() == JavaVisibility.PRIVATE
                && field.isStatic()
                && field.isFinal();
    }
}
