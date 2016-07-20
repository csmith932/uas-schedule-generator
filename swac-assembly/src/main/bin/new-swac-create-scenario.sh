#!/bin/bash

#Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.

#This computer Software was developed with the sponsorship of the U.S. Government
#under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).

LIB_JAVA=${SWAC_HOME}/lib/java

function usage() {
cat << EOF
Usage: `basename $0` scenario_name base_date forecast_fiscal_year [classifier] [-u] [[-f|--files] file_name data_class [...]]
       `basename $0` -h
    Creates the necessary files and directories to run a given scenario.
    
    scenario_name - The name to give to the created scenario.
    base_date - The base date for the scenario (yyyyMMdd).
    forecast_fiscal_year - The forecast fiscal year for the scenario (yyyy).

    classifier - The scenario classifier.

    -f|--files: Override following scenario files
        (
            file_name: The name or the fileglob filter to identify the file(s). 
                       Any valid input to the ls command.
            data_class: The Java class which will be used to read in the 
                        contents of the file.
        )
    -u: Update scenario
        Allows the user to modify an existing scenario. The base dates, forecast 
        fiscal years, classifiers, or overridden files may be changed. NOTE: 
        Any previously overridden files will be reverted to the default unless
        the override is specified again.
    -h: help
        outputs this usage statement.
EOF
    exit $1
}

function file_substitution() {
    TMP="$NEW_SCENARIO_DIR/temp/tmp.txt"

    sed s:"$2":"$3":g "$1" > "$TMP"

    errno="$?"

    if [[ $errno != 0 ]]; then
        echo "FATAL: Error creating scenario file: "${1}". sed exited with errno: ${errno}."
        swac-delete-scenario "$SCENARIO_NAME" 2>/dev/null
        exit $errno
    fi

    mv "$TMP" "$1"

    errno="$?"

    if [[ $errno != 0 ]]; then
        echo "FATAL: Error creating scenario file: "${1}". mv exited with errno: ${errno}."
        swac-delete-scenario "$SCENARIO_NAME" 2>/dev/null
        exit $errno
    fi
}

################################################
# START SCRIPT EXECUTION
################################################
#$1 - Scenario Name
#$2 - Base Date (yyyyMMdd)
#$3 - Forecast Fiscal Year
#$4 - Classifier

if [[ $# < 3 ]]
then
    usage 1
fi 

SCENARIO_NAME="$1"
BASE_DATES="$2"
FORECAST_FISCAL_YEARS="$3"
CLASSIFIERS="base"
F_OPTIONS=""

shift 3

if [[ $# > 0 && $4 != -* ]]; then
    CLASSIFIERS=$1
    shift 1
fi

update_only=false

while getopts "hf:u:-:" OPTION; do
    case $OPTION in
        f) 
            shift 1
            
            F_OPTIONS=$@
            echo "f options ${F_OPTIONS}"
            ;;
        u)
            update_only=true
            ;;
        -)
        	shift 1
        	
        	F_OPTIONS=$@
            echo "f options ${F_OPTIONS}"
            ;;
        h)
            usage 0
            ;;
        *)
            usage 1
            ;;
    esac
done

NEW_SCENARIO_DIR="$SWAC_WORK/scenarios/$SCENARIO_NAME"
NEW_SCENARIO_XML_FILE="$NEW_SCENARIO_DIR/$SCENARIO_NAME.xml"
NEW_SCENARIO_PROPERTIES_FILE="$NEW_SCENARIO_DIR/$SCENARIO_NAME.properties"

if [[ $update_only == true && (-d "$NEW_SCENARIO_DIR") ]]; then
    rm "$NEW_SCENARIO_PROPERTIES_FILE"

    errno=$?

    if [[ $errno != 0 ]]; then
        echo "ERROR: Update to properties file failed! rm exited with status ${errno}"
        exit $errno
    fi

    cp "$SWAC_WORK/scenarios/scenario/scenario.properties" "$NEW_SCENARIO_PROPERTIES_FILE"

    errno=$?

    if [[ $errno != 0 ]]; then
        echo "ERROR: Update to properties file failed! cp exited with status ${errno}"
        exit $errno
    fi
else
    swac-copy-scenario scenario "$SCENARIO_NAME"

    errno=$?

    if [[ $errno != 0 ]]; then
        # At this point, swac-copy-scenario will have already logged the error message:
        # "FATAL: Scenario name is already in use"
        echo "	To delete the existing scenario, run \"swac -x $SCENARIO_NAME\""
        exit $errno
    fi

    echo "INFO: Scenario XML file: ${NEW_SCENARIO_XML_FILE} created."
fi

file_substitution "$NEW_SCENARIO_PROPERTIES_FILE" "BASE_DATE=BASE_DATE" "BASE_DATE=${BASE_DATES}"
file_substitution "$NEW_SCENARIO_PROPERTIES_FILE" "FORECAST_FISCAL_YEAR=FORECAST_FISCAL_YEAR" "FORECAST_FISCAL_YEAR=${FORECAST_FISCAL_YEARS}"
file_substitution "$NEW_SCENARIO_PROPERTIES_FILE" "CLASSIFIER=CLASSIFIER" "CLASSIFIER=${CLASSIFIERS}"

echo "INFO: Scenario properties file: ${NEW_SCENARIO_PROPERTIES_FILE} created."

java -Dswac.work.dir="$SWAC_WORK" -cp "${LIB_JAVA}/"\* gov.faa.ang.swac.controller.ScenarioImportsFileGenerator $SCENARIO_NAME $BASE_DATES $FORECAST_FISCAL_YEARS $CLASSIFIERS $F_OPTIONS

echo "INFO: Finished creating scenario ${SCENARIO_NAME}";
