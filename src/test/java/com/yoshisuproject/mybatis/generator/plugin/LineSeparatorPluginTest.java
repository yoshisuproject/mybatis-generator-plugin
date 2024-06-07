package com.yoshisuproject.mybatis.generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LineSeparatorPluginTest {

    private LineSeparatorPlugin plugin;
    private Properties properties;

    @BeforeEach
    void setUp() {
        plugin = new LineSeparatorPlugin();
        properties = new Properties();
    }

    @Test
    void validateShouldReturnTrue() {
        assertTrue(plugin.validate(new ArrayList<>()));
    }

    @Test
    void setPropertiesWithLF() {
        String originalLineSeparator = System.lineSeparator();

        try {
            properties.setProperty("lineSeparator", "lf");
            plugin.setProperties(properties);

            assertEquals("\n", System.getProperty("line.separator"));
        } finally {
            System.setProperty("line.separator", originalLineSeparator);
        }
    }

    @Test
    void setPropertiesWithCR() {
        String originalLineSeparator = System.lineSeparator();

        try {
            properties.setProperty("lineSeparator", "cr");
            plugin.setProperties(properties);

            assertEquals("\r", System.getProperty("line.separator"));
        } finally {
            System.setProperty("line.separator", originalLineSeparator);
        }
    }

    @Test
    void setPropertiesWithCRLF() {
        String originalLineSeparator = System.lineSeparator();

        try {
            properties.setProperty("lineSeparator", "crlf");
            plugin.setProperties(properties);

            assertEquals("\r\n", System.lineSeparator());
        } finally {
            System.setProperty("line.separator", originalLineSeparator);
        }
    }

    @Test
    void setPropertiesWithSystem() {
        String originalLineSeparator = System.lineSeparator();

        try {
            properties.setProperty("lineSeparator", "system");
            plugin.setProperties(properties);

            assertEquals(originalLineSeparator, System.lineSeparator());
        } finally {
            System.setProperty("line.separator", originalLineSeparator);
        }
    }

    @Test
    void setPropertiesWithInvalidValue() {
        String originalLineSeparator = System.lineSeparator();

        try {
            properties.setProperty("lineSeparator", "invalid");
            plugin.setProperties(properties);

            assertEquals(originalLineSeparator, System.lineSeparator());
        } finally {
            System.setProperty("line.separator", originalLineSeparator);
        }
    }
}
