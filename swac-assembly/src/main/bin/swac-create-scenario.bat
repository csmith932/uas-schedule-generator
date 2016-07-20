@echo off

@setlocal enableDelayedExpansion

set lib_java=%SWAC_HOME%/lib/java

if [%3]==[] (
    echo "Insufficient arguments provided"& call:usage %0& exit /b
) else (
    set scenario_name=%1
    set base_date=%2
    set forecast_fiscal_year=%3
)

if not [%4]==[] (
    java -Dswac.work.dir="%SWAC_WORK%" -cp "%lib_java%/\*" gov.faa.ang.swac.controller.SwacCreateScenario %scenario_name% %base_date% %forecast_fiscal_year% %4 & exit /b
) else (
    java -Dswac.work.dir="%SWAC_WORK%" -cp "%lib_java%/\*" gov.faa.ang.swac.controller.SwacCreateScenario %scenario_name% %base_date% %forecast_fiscal_year% "base" & exit /b
)

:usage
    echo Usage: %1% scenario_name base_date forecast_fiscal_year [classifier]
    echo Creates the necessary files and directories to run a given scenario.
    echo 
    echo scenario_name: The name to give to the created scenario.
    echo base_date: The base date for the scenario (yyyyMMdd).
    echo forecast_fiscal_year: The forecast fiscal year for the scenario (yyyy).
    echo classifier: The scenario classifier (Optional).
    exit /b
