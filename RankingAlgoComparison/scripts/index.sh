javac -d . ../src/*.java
java -Xmx1024m -cp . Index $1 $2 
java -Xmx1024m -cp . CalculateScore $2

