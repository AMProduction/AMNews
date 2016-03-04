#!/bin/bash
mkdir bin
javac -d bin -sourcepath src/com/amproduction/amnews src/com/amproduction/amnews/model/*.java src/com/amproduction/amnews/util/*.java src/com/amproduction/amnews/view/*.java src/com/amproduction/amnews/*.java
mkdir out
cp src/com/amproduction/amnews/view/*.fxml bin/com/amproduction/amnews/view
javapackager -createjar -appclass com.amproduction.amnews.MainApp -srcdir bin -outdir out -outfile AMNews -v
