# apriori4j - Apriori Algorithm Implementation in Java

[Apriori Algorithm](http://en.wikipedia.org/wiki/Apriori_algorithm) implementation in Java which is based on https://github.com/asaini/Apriori/blob/master/apriori.py.

## apriori4j

### How to use

#### Maven

```xml
<dependency>
  <groupId>com.github.seratch</groupId>
  <artifactId>apriori4j</artifactId>
  <version>0.3</version>
</dependency>
```

#### Gradle

```
compile 'com.github.seratch:apriori4j:0.3'
```

#### Example

```java
import apriori4j.*;
import java.util.List;

List<Transaction> transactions = prepareTranscations();
Double minSupport = 0.15;
Double minConfidence = 0.6;

AprioriAlgorithm apriori = new AprioriAlgorithm(minSupport, minConfidence);
AnalysisResult result = apriori.analyze(transactions);
```

## apriori4s

Scala interface is also available for the following Scala binary versions.

- Scala 2.10
- Scala 2.11

### How to use

#### sbt

```scala
libraryDependencies += "com.github.seratch" %% "apriori4s" % "0.3"
```

#### Example

```scala
import apriori4s._

val transactions: Seq[Transaction] = prepareTransactions();
val apriori = AprioriAlgorithm(minSupport = 0.15, minConfidence = 0.6)
val result: AnalysisResult = apriori.analyze(transactions)
```

## License

(The MIT License)

Copyright (c) 2014 - 2015 Kazuhiro Sera
