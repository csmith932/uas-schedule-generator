@echo off

if [%1]==[] (
    echo Scenario name to delete not provided& call:usage %0& exit /b
) else (
    rd /q /s "%SWAC_WORK%"\scenarios\%1& exit /b
)

:usage
    echo "Usage: %1 scenario_name"
    echo
    echo "Removes a scenario and all associated files."
    echo "scenario_name: The name of the scneario to remove."