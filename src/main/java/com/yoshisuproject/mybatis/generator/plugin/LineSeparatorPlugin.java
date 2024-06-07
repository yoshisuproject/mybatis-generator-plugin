package com.yoshisuproject.mybatis.generator.plugin;

import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.PluginAdapter;

/**
 * The plugin to modify line separator configuration used in MyBatis Generator.
 * <p>
 * Supported values for lineSeparator property:
 * <ul>
 *   <li>lf: Unix-style line separator (\n)</li>
 *   <li>cr: Mac-style line separator (\r)</li>
 *   <li>crlf: Windows-style line separator (\r\n)</li>
 *   <li>system: System default line separator</li>
 * </ul>
 */
public class LineSeparatorPlugin extends PluginAdapter {

    /**
     * Sets the line separator configuration.
     * <p>
     * Supported values for lineSeparator property:
     * <ul>
     *   <li>lf: Unix-style line separator (\n)</li>
     *   <li>cr: Mac-style line separator (\r)</li>
     *   <li>crlf: Windows-style line separator (\r\n)</li>
     *   <li>system: System default line separator</li>
     * </ul>
     *
     * @param properties The properties configured for this plugin
     */
    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        String lineSeparator = System.lineSeparator();
        String value = properties.getProperty("lineSeparator");
        switch (value) {
            case "lf":
                lineSeparator = "\n";
                break;
            case "cr":
                lineSeparator = "\r";
                break;
            case "crlf":
                lineSeparator = "\r\n";
                break;
            case "system":
                lineSeparator = System.lineSeparator();
                break;
            default:
                break;
        }

        System.setProperty("line.separator", lineSeparator);
    }

    /**
     * Validates the plugin configuration.
     *
     * @param warnings Warnings during validation will be added to this list
     * @return always true as this plugin is always valid
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
