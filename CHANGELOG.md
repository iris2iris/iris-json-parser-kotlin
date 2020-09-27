# Changelog

## v0.4
- JSON Proxy items added to imitate parsed JSON-nodes
- Added setters
- Added iterable items 

## v0.3
- JSON Flow Parser added (`iris.json.flow`)
- `iris.sequence` package

## v0.2.1
- `IrisJsonItem.asXxx()` optimization
- `IrisJsonItem.asXxxOrNull()` added

## v0.2
- `IrisJsonItem.find` selectors.
- `IrisJsonItem.obj()` fixed. Now it generates full tree of target objects without proxies
- `IrisJsonItem.asInt|asLong|asList|asDouble|asFloat|asBoolean|asString|asObject` methods added
- `IrisJsonString.obj()` fixed handling of escape characters and speed improvement
- `IrisJsonItem.joinTo` method

## v0.1
- Iris JSON parser was created
- Faster JSON-strings parse up to 4x times than `org.json`
