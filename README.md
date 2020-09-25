# iris-json-parser for Kotlin
**Faster up to 4 times** parser comparing to standard org.json because of late objects initialization

Speed improvement is achieved by idea of Proxy pattern, where objects are created when requested.

## Realisations for all languages
- **Kotlin** (main) [iris-json-parser-kotlin](https://github.com/iris2iris/iris-json-parser-kotlin)
- **Java** [iris-json-parser-java](https://github.com/iris2iris/iris-json-parser-java)

## Examples of use

#### Flow pre-parse
🔥 New feature (v0.3). Flow preparing JSON-tree information only until required field.

Useful when required fields are located at first part of JSON string.

```Kotlin
val testString = File("test.json").readText()

// Demonstration of functional abilities
val obj = JsonFlowParser.start(testString) // parsed to IrisJsonItem's

// stringifies result objects
println("IrisJsonItem.toString/JSON string: $obj")

// stringifies objects to Appendable buffer
val b = StringBuilder()
obj.joinTo(b)
println("IrisJsonItem.joinTo/JSON string:   $b")

// Simple access to required object on objects tree
println("IrisJsonItem toString/JSON string: " + obj["object"]["message"]["attachments"][0]["wall"]["id"])

// Converting to required types
println("To Long: " + obj["object"]["message"]["attachments"][0]["wall"]["id"].asLong())

// Access by string path
println("To Int: " + obj.find("object message attachments 0 wall id").asInt())

// Stylized to Java/JavaScript properties access
println("To Double: " + obj.find("object.message.attachments[0].wall.id").asDouble())
```

#### Full pre-parse
Prepares full JSON-tree information. Useful when lots of fields are requested.

```Kotlin
// Demonstration of functional abilities
val parser = IrisJsonParser(testString)
val res = parser.parse() // parsed to IrisJsonItem's

// stringifies result objects
println("IrisJsonItem.toString/JSON string: $res")

// stringifies objects to Appendable buffer
val b = StringBuilder()
res.joinTo(b)
println("IrisJsonItem.joinTo/JSON string:   $res")

// Simple access to required object on objects tree
println("IrisJsonItem toString/JSON string: " + res["object"]["message"]["attachments"][0]["wall"]["id"])

// Converting to required types
println("To Long: " + res["object"]["message"]["attachments"][0]["wall"]["id"].asLong())

// Access by string path
println("To Int: " + res.find("object message attachments 0 wall id").asInt())

// Stylized to Java/JavaScript properties access
println("To Double: " + res.find("object.message.attachments[0].wall.id").asDouble())
```

## Performance test
Test code is in [iris/json/flow/test/performance_test.kt](https://github.com/iris2iris/iris-json-parser-kotlin/blob/master/src/iris/json/flow/test/performance_test.kt) file. 

Test JSON file is in [test_array.json](https://github.com/iris2iris/iris-json-parser-kotlin/blob/master/test_array.json) file.

Testing access to first element of array and access to last element of array.
```
AVG[0]:
org.json:   24706
Iris Plain: 7195
Iris Flow:  98

AVG[49]:
org.json:   24599
Iris Plain: 7187
Iris Flow:  7839
```

Check out [CHANGELOG.md](https://github.com/iris2iris/iris-json-parser-kotlin/blob/master/CHANGELOG.md)

⭐ If this tool was useful for you, don't forget to give star.
