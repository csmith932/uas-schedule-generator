@echo off

set lib_java=%SWAC_HOME%/lib/java

if exist {"%SWAC_WORK%/log"} (
    del "%SWAC_WORK%/log/*"
)

set log_level="VERBOSE"
set gen_kml="FALSE"
set validation_level="NORMAL"
set jvm_max_size="4096m"

if [%1]==[] (
    echo Scenario name not provided& call:usage %0& exit /b
) else (
    set scenario_name=%1
)

if not [%2]==[] (set log_level=%2)
if not [%3]==[] (set gen_kml=%3)
if not [%4]==[] (set validation_level=%4)
if not [%5]==[] (set jvm_max_size="%5%m")
if not [%6]==[] (set debugger_port=%6)

if [%6]==[] (
    java -Xms256m -Xmx%jvm_max_size% -XX:+UseParallelGC -Dswac.work.dir="%SWAC_WORK%" -Dswac.log.level=%log_level% -Dswac.gen.kml=%gen_kml% -Dswac.validation.level=%validation_level% -cp "%lib_java%/\*" gov.faa.ang.swac.controller.Bootstrap %scenario_name%& exit /b
) else (
    java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=%6,suspend=y -Xms256m -Xmx%jvm_max_size% -XX:+UseParallelGC -Dswac.work.dir="%SWAC_WORK%" -Dswac.log.level=%log_level% -Dswac.gen.kml=%gen_kml% -Dswac.validation.level=%validation_level% -cp "%lib_java%/\*" gov.faa.ang.swac.controller.Bootstrap %scenario_name%& exit /b
)

:usage
    echo "Usage: %1% scenario_name [log_level] [gen_kml] [validation_level] [jvm_max_size] [debugging_port]"
    echo
    echo "scenario_name: The name of the scenario to execute (Mandatory)."
    echo "log_level: NONE | VERBOSE (Default)| DEBUG"
    echo "gen_kml: TRUE | FALSE (Default)"
    echo "validation_level: NORMAL (Default) | DEEP | DEEP_NO_EXECUTE"
    echo "jvm_max_size: The maximum amount of memory to allocate to the JVM in megabytes (Default 4096)."
    echo "attach_debugger: If there is a sixth argument; the program will wait for a debugger."
    echo "debugging_port: The port number for the debugger to use. Specifying this will cause the program to"
    echo "    pause and wait for a debugger to attach to the provided port number."
    exit /b

