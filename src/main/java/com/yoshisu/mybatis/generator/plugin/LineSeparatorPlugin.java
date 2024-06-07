package com.yoshisu.mybatis.generator.plugin;

import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.PluginAdapter;

public class LineSeparatorPlugin extends PluginAdapter {

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        String lineSeparator = System.lineSeparator();
        String value = properties.getProperty("lineSeparator");
        switch (value) {
            case "lf" :
                lineSeparator = "\n";
                break;
            case "cr" :
                lineSeparator = "\r";
                break;
            case "crlf" :
                lineSeparator = "\r\n";
                break;
            case "system" :
                lineSeparator = System.lineSeparator();
                break;
            default :
                break;
        }

        System.setProperty("line.separator", lineSeparator);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
