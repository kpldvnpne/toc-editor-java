mac='dmg'
windows='exe'
linux='deb'

jpackage --input executable/ \
  --name toc-editor \
  --main-jar toc-editor.jar \
  --main-class Main \
  --type $mac \
  --dest executable/
