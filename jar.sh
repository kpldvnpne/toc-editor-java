# Alternative: java  -cp "./iText-Core-9.2.0-only-jars/kernel-9.2.0.jar" Main.java

MAIN_DIR=$(pwd)

OUT_DIR=build/classes
DEPS_OUT_DIR=build/lib-extracted

# Remove existing deps output
rm -rf $DEPS_OUT_DIR

# Class paths --- START
# iText only
bouncy_castle_adapter='lib/itext/bouncy-castle-adapter-9.2.0.jar'
commons='lib/itext/commons-9.2.0.jar'
io='lib/itext/io-9.2.0.jar'
kernel='lib/itext/kernel-9.2.0.jar'
layout='lib/itext/layout-9.2.0.jar'

# iText dependencies
slf4j='lib/slf4j-api-2.0.17.jar'

# Combine
itext_cp="$kernel:$io:$layout:$bouncy_castle_adapter:$commons"
whole_cp="$itext_cp:$slf4j"
# Class paths --- END

## Make jar
# Get class paths as array
IFS=':'; cp_array=($whole_cp); unset IFS;

return_dir=$(pwd) # Store return dir
mkdir $DEPS_OUT_DIR # Make out dir if not made
cd $DEPS_OUT_DIR # Change to the deps out dir

# Extract to deps out dir
for classpath in "${cp_array[@]}"
do
  jar -xf $MAIN_DIR/$classpath
done
cd $return_dir # Return back to the return dir

# Bundle into jar
jar cfm build/jar/TocEditor.jar manifest.mf -C $OUT_DIR . -C build/lib-extracted .

# For mac
# TODO: See if icons set from resources work
# TODO: Does file associations work?
jpackage --input build/jar  --main-jar TocEditor.jar --type dmg \
  --description "Allows you to edit the table of content of any PDFs" \
  --name "PDF Table of Content Editor" --dest build/exec --app-version 1.0 \
  --icon 'resources/PDF Table of Content Editor.icns'
  # --resource-dir 'resources'
  # --file-associations FApdf.properties \
