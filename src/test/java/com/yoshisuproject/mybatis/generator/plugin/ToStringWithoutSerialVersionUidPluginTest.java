package com.yoshisuproject.mybatis.generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;

class ToStringWithoutSerialVersionUidPluginTest {

    private ToStringWithoutSerialVersionUidPlugin plugin;
    private Properties properties;
    private List<String> warnings;
    private TopLevelClass topLevelClass;

    @Mock
    private IntrospectedTable introspectedTable;

    @Mock
    private Context context;

    @Mock
    private CommentGenerator commentGenerator;

    @BeforeEach
    void setUp() {
        plugin = new ToStringWithoutSerialVersionUidPlugin();
        properties = new Properties();
        warnings = new ArrayList<>();

        topLevelClass = new TopLevelClass("com.example.TestClass");

        Field idField = new Field("id", FullyQualifiedJavaType.getIntInstance());
        idField.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(idField);

        Field nameField = new Field("name", FullyQualifiedJavaType.getStringInstance());
        nameField.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(nameField);

        Field serialVersionUID = new Field("serialVersionUID", new FullyQualifiedJavaType("long"));
        serialVersionUID.setStatic(true);
        serialVersionUID.setFinal(true);
        serialVersionUID.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(serialVersionUID);

        topLevelClass.addSuperInterface(new FullyQualifiedJavaType("java.io.Serializable"));

        introspectedTable = Mockito.mock(IntrospectedTable.class);
        context = Mockito.mock(Context.class);
        commentGenerator = Mockito.mock(CommentGenerator.class);

        when(introspectedTable.getTargetRuntime()).thenReturn(IntrospectedTable.TargetRuntime.MYBATIS3);
        when(introspectedTable.getContext()).thenReturn(context);
        when(context.getCommentGenerator()).thenReturn(commentGenerator);

        plugin.setContext(context);
    }

    @Test
    void validateWithDefaultSign() {
        assertTrue(plugin.validate(warnings));
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("Used default PAREN sign"));
    }

    @Test
    void validateWithValidOpenSign() {
        properties.setProperty("openSign", "(");
        plugin.setProperties(properties);

        assertTrue(plugin.validate(warnings));
        assertEquals(0, warnings.size());
    }

    @Test
    void validateWithInvalidOpenSign() {
        properties.setProperty("openSign", "$");
        plugin.setProperties(properties);

        assertTrue(plugin.validate(warnings));
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("Used defalut PAREN sign"));
    }

    @Test
    void validateWithValidSign() {
        properties.setProperty("sign", "BRACKET");
        plugin.setProperties(properties);

        assertTrue(plugin.validate(warnings));
        assertEquals(0, warnings.size());
    }

    @Test
    void validateWithInvalidSign() {
        properties.setProperty("sign", "INVALID");
        plugin.setProperties(properties);

        assertTrue(plugin.validate(warnings));
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("Used default PAREN sign"));
    }

    @Test
    void generateToStringWithDefaultSettings() {
        plugin.validate(warnings);
        plugin.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);

        Method toStringMethod = getToStringMethod(topLevelClass);
        assertNotNull(toStringMethod);

        List<String> bodyLines = toStringMethod.getBodyLines();
        assertTrue(bodyLines.contains("sb.append(\"(\");"));
        assertTrue(bodyLines.contains("sb.append(\")\");"));
        assertTrue(bodyLines.contains("sb.append(\"id=\").append(id);"));
        assertTrue(bodyLines.contains("sb.append(\", name=\").append(name);"));
        assertFalse(bodyLines.toString().contains("serialVersionUID"));
    }

    @Test
    void generateToStringWithBracketSign() {
        properties.setProperty("sign", "BRACKET");
        plugin.setProperties(properties);
        plugin.validate(warnings);
        plugin.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);

        Method toStringMethod = getToStringMethod(topLevelClass);
        assertNotNull(toStringMethod);

        List<String> bodyLines = toStringMethod.getBodyLines();
        assertTrue(bodyLines.contains("sb.append(\"[\");"));
        assertTrue(bodyLines.contains("sb.append(\"]\");"));
    }

    @Test
    void generateToStringWithSuperClass() {
        properties.setProperty("useToStringFromRoot", "true");
        plugin.setProperties(properties);
        plugin.validate(warnings);

        topLevelClass.setSuperClass(new FullyQualifiedJavaType("com.example.BaseClass"));

        plugin.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);

        Method toStringMethod = getToStringMethod(topLevelClass);
        assertNotNull(toStringMethod);

        List<String> bodyLines = toStringMethod.getBodyLines();
        assertTrue(bodyLines.contains("sb.append(\", from super class \");"));
        assertTrue(bodyLines.contains("sb.append(super.toString());"));
    }

    private Method getToStringMethod(TopLevelClass topLevelClass) {
        return topLevelClass.getMethods().stream()
                .filter(method -> method.getName().equals("toString"))
                .findFirst()
                .orElse(null);
    }
}
