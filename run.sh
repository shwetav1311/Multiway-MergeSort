#!/bin/bash


javac -sourcepath src -d bin src/**/**/**/*.java

java -Xmx1024m -cp bin: com.iiit.db.Driver $*

