# Alternative: java  -cp "./iText-Core-9.2.0-only-jars/kernel-9.2.0.jar" Main.java

OUT_DIR=build/classes

# Remove existing output
rm -rf $OUT_DIR/*

# Class paths --- START
# iText only
bouncy_castle_adapter='lib/itext/bouncy-castle-adapter-9.2.0'
commons='lib/itext/commons-9.2.0.jar'
io='lib/itext/io-9.2.0.jar'
kernel='lib/itext/kernel-9.2.0.jar'
layout='lib/itext/layout-9.2.0'

# iText dependencies
slf4j='lib/slf4j-api-2.0.17.jar'

# Combine
itext_cp="$kernel:$io:$layout:$bouncy_castle_adapter:$commons"
whole_cp="$itext_cp:$slf4j"
# Class paths --- END

javac -sourcepath src -classpath $whole_cp -d $OUT_DIR src/Main.java
java -classpath "$OUT_DIR:$whole_cp" Main
