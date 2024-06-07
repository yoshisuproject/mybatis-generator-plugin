package com.yoshisuproject.mybatis.generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.config.Context;

@ExtendWith(MockitoExtension.class)
class OptionalPluginTest {

    @Mock
    private Context context;

    @Mock
    private IntrospectedTable introspectedTable;

    @Mock
    private CommentGenerator commentGenerator;

    private OptionalPlugin plugin;

    private Interface interfaze;

    @BeforeEach
    void setUp() {
        plugin = new OptionalPlugin();
        interfaze = new Interface(new FullyQualifiedJavaType("com.example.TestMapper"));

        plugin.setContext(context);
        Properties properties = new Properties();
        properties.setProperty("optionalMethodPrefix", "findOptional");
        plugin.setProperties(properties);

        plugin.validate(new ArrayList<>());
    }

    @Test
    void testValidate() {
        List<String> warnings = new ArrayList<>();
        assertTrue(plugin.validate(warnings));
        assertTrue(warnings.isEmpty());
    }

    @Test
    void testClientSelectByPrimaryKeyMethodGenerated() {
        when(context.getCommentGenerator()).thenReturn(commentGenerator);

        Method method = new Method("selectByPrimaryKey");
        method.setReturnType(new FullyQualifiedJavaType("com.example.User"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("java.lang.Long"), "id"));

        boolean result = plugin.clientSelectByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);

        assertTrue(result);
        assertEquals(1, interfaze.getMethods().size());

        Method generatedMethod = interfaze.getMethods().get(0);
        assertEquals("findOptionalByPrimaryKey", generatedMethod.getName());
        assertEquals(
                "Optional<com.example.User>",
                generatedMethod.getReturnType().get().getFullyQualifiedName());
        assertTrue(interfaze.getImportedTypes().contains(new FullyQualifiedJavaType("java.util.Optional")));

        verify(commentGenerator).addGeneralMethodComment(any(Method.class), eq(introspectedTable));
    }

    @Test
    void testClientSelectByExampleWithoutBLOBsMethodGenerated_NonListReturn() {
        when(context.getCommentGenerator()).thenReturn(commentGenerator);

        Method method = new Method("selectByExample");
        method.setReturnType(new FullyQualifiedJavaType("com.example.User"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("com.example.UserExample"), "example"));

        boolean result = plugin.clientSelectByExampleWithoutBLOBsMethodGenerated(method, interfaze, introspectedTable);

        assertTrue(result);
        assertEquals(1, interfaze.getMethods().size());

        Method generatedMethod = interfaze.getMethods().get(0);
        assertEquals("findOptionalByExample", generatedMethod.getName());
        assertEquals(
                "Optional<com.example.User>",
                generatedMethod.getReturnType().get().getFullyQualifiedName());

        verify(commentGenerator).addGeneralMethodComment(any(Method.class), eq(introspectedTable));
    }

    @Test
    void testCustomMethodPrefix() {
        when(context.getCommentGenerator()).thenReturn(commentGenerator);

        Properties properties = new Properties();
        properties.setProperty("optionalMethodPrefix", "getOptional");
        plugin.setProperties(properties);
        plugin.validate(new ArrayList<>());

        Method method = new Method("selectByPrimaryKey");
        method.setReturnType(new FullyQualifiedJavaType("com.example.User"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("java.lang.Long"), "id"));

        plugin.clientSelectByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);

        Method generatedMethod = interfaze.getMethods().get(0);
        assertEquals("getOptionalByPrimaryKey", generatedMethod.getName());

        verify(commentGenerator).addGeneralMethodComment(any(Method.class), eq(introspectedTable));
    }
}
