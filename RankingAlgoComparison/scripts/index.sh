javac -d . ../src/brij/iiit/iremajor/core/*.java
java -Xmx1024m -cp . brij.iiit.iremajor.core.Index $1 $2 
java -Xms1024m -Xmx4096m -cp . brij.iiit.iremajor.core.CalculateScore $2

