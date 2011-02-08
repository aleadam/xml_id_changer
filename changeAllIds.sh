#!/bin/sh
if [ ! -d "$2" ]; then
mkdir -vp "$2"
fi
cd $1
for FILEIN in *.smali; 
do 
echo java -jar ../XmlIdChanger.jar "${FILEIN}" ../"$2"/"$FILEIN" ../"$3" ../"$4"
java -jar ../XmlIdChanger.jar "${FILEIN}" ../"$2"/"$FILEIN" ../"$3" ../"$4"
done

