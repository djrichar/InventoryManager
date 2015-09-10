# InventoryManager

1. after checkout run ./gradlew fatJar.   This will generate the executable jar you can run from the commandline.
2. next run the following command to execute the jar
```
cd to build/libs && java -jar InventoryManager-all-1.0-SNAPSHOT.jar
```

output format is as follows
```
Header:<id>-<streamid>:[
  <name>:[<askfor>,<got>,<backordered>]{<currentlyInStock>,<currentlyBackOrdered>},
  ...
]
```

you'll find my sampleRunOutput here: https://github.com/djrichar/InventoryManager/blob/master/src/test/resources/samplerun.out
