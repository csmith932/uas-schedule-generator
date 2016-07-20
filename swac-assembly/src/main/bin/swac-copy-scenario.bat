@echo off

set lib_java=%SWAC_HOME%/lib/java

if [%1]==[] (
    echo Base scenario name not provided& call:usage %0& exit /b
) else (
    set base_scenario_name=%1
)

if [%2]==[] (
    echo New scenario name not provided& call:usage %0& exit /b
) else (
    java -Dswac.work.dir="%SWAC_WORK%" -cp "%lib_java%/\*" gov.faa.ang.swac.controller.SwacCopyScenario %base_scenario_name% %2& exit /b
)

:usage
    echo "Usage: %1% base_scenario_name new_scenario_name"
    echo
    echo "Copies the properties and settings from a previously created scenario to create a new scenario."
    echo "base_scenario_name: The name of the source scenario."
    echo "new_scenario_name: The name to give the new scenario."
    exit /b