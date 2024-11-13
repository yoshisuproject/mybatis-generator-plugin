## MyBatis Generator Plugin

Here are three plugins for MyBatis Generator

-   ### LineSeparatorPlugin

    This plugin has sets the line separator for generated files.

    -   lineSeparator (optional): Supports the following settings
        -   lf : Unix
        -   cr : Classic MacOS
        -   crlf : Windows
        -   system : System Dependent

    If not set, it defaults to the System Dependent.

-   ### ToStringWithoutSerivalVersionUidPlugin

    Configures the `toString` method of generated models to exclude the `serialVersionUID` and sets the delimiter between class name and properties.

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

-   ### OptionalPlugin

    This plugin generates additional methods that return `Optional<T>` for select operations.

    For example, if you have a method `User selectByPrimaryKey(Long id)`,
    it will generate an additional method `Optional<User> selectOptionalByPrimaryKey(Long id)`.

    -   optionalMethodPrefix (optional): The prefix for the generated Optional method
        -   Default value: "selectOptional"
        -   Example: If set to "findOptional", the generated method would be `Optional<User> findOptionalByPrimaryKey(Long id)`

    This plugin will:
    -   Generate Optional methods for single-result select operations
    -   Skip List-return-type methods
    -   Automatically import java.util.Optional

## Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>
    <context id="mybatisgenerator" targetRuntime="MyBatis3" defaultModelType="hierarchical">
        <!-- other settings -->
        <plugin type="com.yoshisu.mybatis.generator.plugin.LineSeparatorPlugin">
            <property name="lineSeparator" value="lf"/>
        </plugin>
        <plugin type="com.yoshisu.mybatis.generator.plugin.ToStringWithoutSerialVersionUidPlugin">
            <property name="openSign" value="("/>
        </plugin>
        <plugin type="com.yoshisu.mybatis.generator.plugin.OptionalPlugin">
            <property name="optionalMethodPrefix" value="findOptional"/>
        </plugin>
        <!-- other settings -->
    </context>
    <!-- other settings -->
</generatorConfiguration>
```
