# olca-flowmaps
This is an experimental project for developing and testing the new back-end for
handling flow mappings in openLCA.

## Idea
The idea is to extend the [JSON-LD based openLCA data exchange format](https://github.com/GreenDelta/olca-schema)
to support a structured way to exchange mapping lists of (elementary) flows as
JSON files. Such a mapping file will contain some meta-data of the mapping list
and a set of flow mappings which describe the mapping of flows of two reference
systems. The following shows a first draft of this format:

```javascript
{
  "@id": "id of the mapping list",
  "name": "name of the mapping list",
  "description": "...",
  "sourceSystem": "the old elementary flow system that should be replaced",
  "targetSystem": "the new elementary flow system which should be used",
  "mappings": [
    {
      // `from` describes a flow of the source system
      "from": {
        // the flow reference is required
        "flow": {
          "@id": "...",
          // ...
        },
        // the flow property is optional; the reference flow property of the
        // flow is taken by default
        "flowProperty": {
          "@id": "...",
          // ...
        },
        // also, the unit reference is optional; the reference unit of the
        // unit group of the flow property is taken by default
        "unit": {
          "@id": "...",
          // ...
        }
      },
      // `to` describes the corresponding flow of the target system;
      // the format is the same as in `from`
      "to": {
        "flow": {},
        "flowProperty": {},
        "unit": {}
      },
      // an optional conversion factor which is applied to the amounts of
      // the source flow to convert them into the corresponding amounts of
      // the target flow (in the respective flow properties and units);
      // defaults to 1.0
      "conversionFactor": 1.0
    }
  ]
}
```

These mappings can be then added to a JSON-LD package (e.g. under a folder
`mappings`) together with the flows of the target reference system
(e.g. [Federal LCA Commons](https://github.com/USEPA/Federal-LCA-Commons-Elementary-Flow-List)).
A new view will be implemented in [openLCA](https://github.com/GreenDelta/olca-app)
with which such a JSON-LD package with reference flows, mappings, and related
data sets (flow properties, unit groups, etc.) can be viewed and applied to the
current database (in future versions, this view could be extended to include
editing functions).

Errors like missing flow properties etc. will be indicated in the mapping table
(similar like in the database parameter view that was currently implemented:
https://github.com/GreenDelta/olca-app/issues/69). The mapping functions will be
implemented in the openLCA core framework so that this can be also run in
headless mode (e.g. from the API, which then could be used in the
[olca-conversion-service](https://github.com/GreenDelta/olca-conversion-service)).
