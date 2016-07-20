@echo off

set lib_java=%SWAC_HOME%/lib/java

java -Xms256m -XX:+UseParallelGC -Dswac.work.dir="%SWAC_WORK%" -Dswac.data.extraction="TRUE" -cp "%lib_java%/\*" gov.faa.ang.swac.controller.Bootstrap