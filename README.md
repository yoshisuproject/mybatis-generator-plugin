## MyBatis Generator Plugin

A collection of utility plugins for MyBatis Generator to enhance code generation capabilities. These plugins help with line ending standardization, toString method customization, and Optional type integration.

## Prerequisites
- MyBatis Generator 1.4.0 or above
- Java 8 or above

## Plugins

-   ### LineSeparatorPlugin

    This plugin has sets the line separator for generated files.

    #### Configuration Options

    -   lineSeparator (optional): Supports the following settings
        -   lf : Unix
        -   cr : Classic MacOS
        -   crlf : Windows
        -   system : System Dependent (default)

        If not set, it defaults to the System Dependent.

    #### Example Output
    ```java
    public class User {
        private Long id;¶
        private String name;¶
    }
    ```

-   ### ToStringWithoutSerivalVersionUidPlugin

    Configures the `toString` method of generated models to exclude the `serialVersionUID` and sets the delimiter between class name and properties.

    #### Configuration Options
    -   useToStringFromRoot (optional): If you have specified root class, you can use this property to call super toString method for print fields of root class. It can be useful for tables with inheritance relation.
    -   openSign (optional): Sets the open delimiter.
        -   `(`
        -   `[`
        -   `{`
        -   `<`
    -   sign (optional): Sets the delimiter.
        -   `PAREN` : use `(` and `)`
        -   `BRACKET` : use `[` and `]`
        -   `BRACE` : use `{` and `}`
        -   `THAN_SIGN` : use `<` and `>`

    #### Example Output
    ```java
    @Override
    public String toString() {
        return "User(id=1, name=John)"; // With PAREN style
        // or
        // return "User[id=1, name=John]"; // With BRACKET style
    }
    ```

-   ### OptionalPlugin

    This plugin generates additional methods that return `Optional<T>` for select operations.

    #### Configuration Options
    -   optionalMethodPrefix (optional): The prefix for the generated Optional method
        -   Default value: "selectOptional"
        -   Example: If set to "findOptional", the generated method would be `Optional<User> findOptionalByPrimaryKey(Long id)`

    #### Example Output
    ```java
    // Original method
    // User selectByPrimaryKey(Long id);

    // Generated additional method
    Optional<User> selectOptionalByPrimaryKey(Long id);
    ```

## Configuration Example

Your `generatorConfig.xml` file should look like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
    "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>
    <context id="mybatisgenerator" targetRuntime="MyBatis3" defaultModelType="hierarchical">
        <!-- other settings -->
        <plugin type="com.yoshisuproject.mybatis.generator.plugin.LineSeparatorPlugin">
            <property name="lineSeparator" value="lf"/>
        </plugin>
        <plugin type="com.yoshisuproject.mybatis.generator.plugin.ToStringWithoutSerialVersionUidPlugin">
            <property name="openSign" value="("/>
        </plugin>
        <plugin type="com.yoshisuproject.mybatis.generator.plugin.OptionalPlugin">
            <property name="optionalMethodPrefix" value="findOptional"/>
        </plugin>
        <!-- other settings -->
    </context>
    <!-- other settings -->
</generatorConfiguration>
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
