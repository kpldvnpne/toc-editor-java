# javac -verbose -classpath "./lib/itext/kernel-9.2.0.jar" ./src/Main.java -d ./bin
# javac -classpath "./lib/try.jar" ./src/Main.java -d ./bin

OUT_DIR=bin

# Remove existing output
rm -rf $OUT_DIR/*

# Class paths --- START
slf4j='lib/slf4j-api-2.0.17.jar'

kernel='lib/itext/kernel-9.2.0.jar'
io='lib/itext/io-9.2.0.jar'
layout='lib/itext/layout-9.2.0'
bouncy_castle_adapter='lib/itext/bouncy-castle-adapter-9.2.0'

itext_cp="$kernel:$io:$layout:$bouncy_castle_adapter"
whole_cp="$itext_cp:$slf4j"
# Class paths --- END

javac -cp $whole_cp -d $OUT_DIR src/*.java
