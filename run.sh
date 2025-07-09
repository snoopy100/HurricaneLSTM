rm -rf target
mvn clean package
cd target
mv HurricaneLSTM-1.0-SNAPSHOT.jar ../
cd ..
java -jar HurricaneLSTM-1.0-SNAPSHOT.jar