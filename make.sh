#!/bin/bash
mkdir bin
javac -d bin -sourcepath src src/model/*.java src/util/*.java src/view/*.java src/*.java
mkdir out
cp src/view/*.fxml bin/com/amproduction/amnews/view
javapackager -createjar -appclass com.amproduction.amnews.MainApp -srcdir bin -outdir out -outfile AMNews -v
