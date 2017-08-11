# IntelliJ IDEA externalizable code generation plugin #

[![Repository](https://img.shields.io/badge/IntelliJ%20IDEA-Plugin-brightgreen.svg?style=flat-square)](https://plugins.jetbrains.com/plugin/9847-externalizable-generator)
[![GitHub release](https://img.shields.io/github/release/nolequen/idea-externalizable-plugin.svg?style=flat-square)](https://github.com/nolequen/idea-externalizable-plugin/releases/latest)
[![Codebeat](https://codebeat.co/badges/814f3f04-8e8c-4f44-8b7d-378d8b11415f)](https://codebeat.co/projects/github-com-nolequen-idea-externalizable-plugin-master)
Despite that manual implementation of the [Externalizable](https://docs.oracle.com/javase/8/docs/api/java/io/Externalizable.html "Javadoc") interface gives you complete control in reading and writing objects during Java serialization, it also requires attentiveness and additional work in support.

However if used correctly it can help to improve serialization process performance.

The plugin allows to generate externalizable code for Java classes. Easy and safe.

## Installation ##

You may install the plugin following [official guide](https://www.jetbrains.com/help/idea/installing-updating-and-uninstalling-repository-plugins.html) or download it from [JetBrains Plugin Repository](https://plugins.jetbrains.com/plugin/9847-externalizable-generator).

## Usage ##

After installation you can find `Externalizable` action in the Generate menu (`Alt + Ins`, by default).

For example our class is:
```java
public class Example {
  private int intValue;
  private Integer boxedInteger;
  private Object object;

  /*...*/
}
```
The plugin will generate following code:
```java
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Example implements Externalizable {
  private int intValue;
  private Integer boxedInteger;
  private Object object;

  public Example() {
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    intValue = in.readInt();
    if (in.readBoolean()) {
      boxedInteger = in.readInt();
    }
    if (in.readBoolean()) {
      object = in.readObject();
    }
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeInt(intValue);
    if (boxedInteger == null) {
      out.writeBoolean(false);
    } else {
      out.writeBoolean(true);
      out.writeInt(boxedInteger);
    }
    if (object == null) {
      out.writeBoolean(false);
    } else {
      out.writeBoolean(true);
      out.writeObject(object);
    }
  }
}

```

## Benchmarks ##

...to be described
