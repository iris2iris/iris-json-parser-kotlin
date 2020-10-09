# iris-json-parser for Kotlin
**Faster up to 4 times** parser comparing to standard org.json because of late objects initialization

Speed improvement is achieved by idea of Proxy pattern, where objects are created when requested.

## Realisations for all languages
- **Kotlin** (main) [iris-json-parser-kotlin](https://github.com/iris2iris/iris-json-parser-kotlin)
- **Java** [iris-json-parser-java](https://github.com/iris2iris/iris-json-parser-java)

## Examples of use

#### Deserialization
🔥 New feature (v0.5). Deserialization to objects.

Full source code on [iris/json/test/serialization.kt](https://github.com/iris2iris/iris-json-parser-kotlin/blob/master/src/iris/json/test/serialization.kt)
````kotlin
val item = IrisJsonParser("""{"id": 3, 
		|"person1": {"name": "Akbar", "age": 35, "cashAmount": 12200.12, "property": {"name": "Домик в деревне"}}, 
		|"type": "MaleFirst", 
		|"person2": {"name": "Alla Who", "height": 170, "income": 1214.81}
		|}""".trimMargin()).parse()

val user: User = item.asObject<User>()
println(user.person1)
println(user.person2)
````

#### Flow pre-parse
Interesting feature (v0.3). Flow preparing JSON-tree information only until required field.

Useful when required fields are located at first part of JSON string.

```kotlin
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

```kotlin
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

#### Array of 50 elements
Test code is in [iris/json/test/performance_array.kt](https://github.com/iris2iris/iris-json-parser-kotlin/blob/master/src/iris/json/test/performance_array.kt) file. 

Test JSON file is in [test_array.json](https://github.com/iris2iris/iris-json-parser-kotlin/blob/master/test_array.json) file.

Testing access to first element of array and access to last element of array. 100k iterations
```
AVG[0]:
org.json:   22363
org.json.simple: 27080
Iris Plain: 5394 // previous 7110
Iris Flow:  564 // previous 93
Iris Proxy: 27
POJO:       11

AVG[49]:
org.json:   22416
org.json.simple: 26869
Iris Plain: 5411 // previous 7067
Iris Flow:  5870 // previous 7498
Iris Proxy: 26
POJO:       10
```

#### Complex json-tree structure

Test code is in [iris/json/test/performance_object_tree.kt](https://github.com/iris2iris/iris-json-parser-kotlin/blob/master/src/iris/json/test/performance_object_tree.kt) file. 

Test JSON file is in [test.json](https://github.com/iris2iris/iris-json-parser-kotlin/blob/master/test.json) file.

Testing access to `object.message.attachments[0].wall.id` and converting it to Long. 100k iterations
```
org.json:   9149
org.json.simple: 11186
Iris Plain: 2652
Iris Flow:  617
Iris Proxy: 53
POJO:       21
```

Check out [CHANGELOG.md](https://github.com/iris2iris/iris-json-parser-kotlin/blob/master/CHANGELOG.md)

⭐ If this tool was useful for you, don't forget to give star.
