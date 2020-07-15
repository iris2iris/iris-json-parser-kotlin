# Changelog

## v 0.2
- `IrisJsonItem.find` selectors.
- `IrisJsonItem.obj()` fixed. Now it generates full tree of target objects without proxies
- `IrisJsonItem.asInt|asLong|asList|asDouble|asFloat|asBoolean|asString|asObject` methods added
- `IrisJsonString.obj()` fixed handling of escape characters and speed improvement
- `IrisJsonItem.joinTo` method

## v 0.1
- Iris JSON parser was created
- Faster JSON-strings parse up to 4x times than `org.json`