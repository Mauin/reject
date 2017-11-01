# reject

Dependency Rejection for Java and Android

_"Sometimes you have something that you don't want called. So you can simply annotate it with `@Reject`"_

## Dependency Rejection

Inspired by a **comedy** talk from Chet Haase and Romain Guy at droidcon London 2017 where they presented
a "new programming language". The **F**unctional **A**nd **R**eactive **T**uring-complete language.

The language comes with lots of useful features. One of them is dependency rejection. Such a helpful new feature shouldn't be limited to this particular language.
`reject` ports the dependency rejection functionality and the `@Reject` annotation to Java and Android to make your projects clearer,
simpler, and more productive.

`reject` gives you access to the `@Reject` annotation in your Java or Android project. It tells the compiler to reject certain things and it lets you
know accordingly.

```
@Reject
public MyClass() { }

> gradle run
No.
```


## Usage

### Import the plugin

```
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath "com.mtramin.reject:gradle-plugin:<reject-version>"
  }
}

apply plugin: 'reject'

```

### Rejecting things

Simply annotate methods or constructors with the `@Reject` annotation to reject those.

```
public MyClass {

    @Reject
    public MyClass() {
        System.out.println("Hello World")
    }

    @Reject
    private void something() {
        ...
    }
}
```

The compiler will then properly reject those classes/methods by simply saying "No.".

### Samples

The samples showcase how to import `reject` and how to use it in Java and Android projects.

#### Java Sample

Run the Java sample with the following command and watch the output in the command line:

`./gradlew :samples:sample-java:run`

#### Android Sample

Build and run the sample application on an Android emulator or device and watch the logcat for the results.


## Credits
- Thanks to [Chet Haase](https://twitter.com/chethaase) and [Romain Guy](https://twitter.com/romainguy) for their comedy talk at droidcon London 2017. You can watch it
[here](https://skillsmatter.com/skillscasts/10764-looking-forward-to-chet-haase-and-romain-guy-comedy-talk).
- [eveoh](https://github.com/eveoh): For the [Gradle AspectJ Plugin](https://github.com/eveoh/gradle-aspectj)
