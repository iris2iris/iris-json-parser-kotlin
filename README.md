# iris-json-parser for Kotlin
**Faster up to 4 times** parser comparing to standard org.json because of late objects initialization

Speed improvement is achieved by idea of Proxy pattern, where objects are created when requested.

## Realisations for all languages
- **Kotlin** (main) [iris-json-parser-kotlin](https://github.com/iris2iris/iris-json-parser-kotlin)
- **Java** [iris-json-parser-java](https://github.com/iris2iris/iris-json-parser-java)

## Examples of use
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


Check out [CHANGELOG.md](https://github.com/iris2iris/iris-json-parser-kotlin/blob/master/CHANGELOG.md)

⭐ If this tool was useful for you, don't forget to give star.
