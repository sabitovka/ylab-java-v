# Logging Starter

**Logging Starter** - это стартер для логирования вызова методов с замеров времени их выполнения

## Приступая к работе
### Требования

Убедитесь, что у вас установлены следующие компоненты:
- Oracle Java 17
- Apache Maven 3.9.8

### Установка
1. Клонируйте репозиторий и перейдите в папку с проектом
   ```shell
   git clone https://github.com/sabitovka/logging-starter.git && cd logging-starter
   ```
2. Скомпилируйте и установите библиотеку в локальный репозиторий
   ```shell
   mvn clean compile install
   ```
3. Подключите зависимость в своем проекте
   ```xml
   <dependency>
      <groupId>io.sabitovka.starter</groupId>
      <artifactId>logging-starter</artifactId>
      <version>1.0.0</version>
   </dependency>
   ```

## Использование
Вы можете использовать аннотацию `@Loggable` для отметки, что метод требует логирования.
```java
public class Foo {
   @Loggable
   public String doSomeMethod(Long bar) {
       // ...
   }
}
```

Это также работает и с классом

```java
@Loggable
public class Foo {
   public String doSomeMethod(Long bar) {
       // ...
   }
}
```

Результат в консоли
```text
Выполнен метод String your.package.Foo.doSomeMethod(Long) [16 ms].
```
