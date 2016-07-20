#!/bin/bash

#Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.

#This computer Software was developed with the sponsorship of the U.S. Government
#under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).

function usage() {
cat << EOF
Usage: `basename $0` base_scenario_name new_scenario_name
    Copies the properties and settings from a previously created scenario to 
    create a new scenario.

        base_scenario_name - The name of the source scenario.
        new_scenario_name - The name to give the new scenario.
EOF
    exit $1
}

function create_directory() {
    mkdir -p "$NEW_SCENARIO_DIR/$1" 2>/dev/null

    errno=$?

    # Verify directory creation succeeded
    if [[ $errno != 0 ]]; then
        echo "FATAL: Could not create scenario sub-directory ${NEW_SCENARIO_DIR}/${1}. cp exited with errno: ${errno}."
        exit $errno
    else
        echo "INFO: Scenario sub-directory ${NEW_SCENARIO_DIR}/${1} created."
    fi
}

function copy_file() {
    cp "$1" "$2"

    errno=$?

    # Verify copying file succeeded
    if [[ $errno != 0 ]]; then
        echo "FATAL: Could not copy scenario file (${1}). cp exited with errno: ${errno}."
        exit $errno
    else
        echo "INFO: Scenario file ${1} copied."
    fi
}

function file_substitution() {
    TMP="$NEW_SCENARIO_DIR/temp/tmp.txt"

    sed -e s:"$2":"$3":g "$1" > "$TMP"

    errno=$?

    if [[ $errno != 0 ]]; then
        echo "FATAL: Error creating scenario file: ${1}. sed exited with errno: ${errno}."
        swac-delete-scenario $SCENARIO_NAME 2>/dev/null
        exit $errno
    fi

    mv "$TMP" "$1"

    errno=$?

    if [[ $errno != 0 ]]; then
        echo "FATAL: Error creating scenario file: ${1}. mv exited with errno: ${errno}."
        swac-delete-scenario $SCENARIO_NAME 2>/dev/null
        exit $errno
    fi
}

#$1 - Base Scenario Name
#$2 - New Scenario Name

if [[ $# < 2 ]]; then
    usage 1
fi 

BASE_SCENARIO_NAME="$1"
NEW_SCENARIO_NAME="$2"

BASE_SCENARIO_DIR="$SWAC_WORK/scenarios/$BASE_SCENARIO_NAME"
BASE_SCENARIO_XML_FILE="$BASE_SCENARIO_DIR/$BASE_SCENARIO_NAME.xml"
BASE_SCENARIO_PROPERTIES_FILE="$BASE_SCENARIO_DIR/$BASE_SCENARIO_NAME.properties"

# Verify base scenario directory exists and is readable
if [[ !(-d "$BASE_SCENARIO_DIR") && !(-r "$BASE_SCENARIO_DIR") ]]; then
    echo "FATAL: Base scenario directory does not exist or is not readable."
    exit 1
fi

# Verify base scenario xml file exists and is readable
if [[ !(-e "$BASE_SCENARIO_XML_FILE") && !(-r "$BASE_SCENARIO_XML_FILE") ]]; then
    echo "FATAL: Base scenario xml file does not exist or is not readable."
    exit 1
fi

# Verify base scenario properties file exists and is readable
if [[ !(-e "$BASE_SCENARIO_PROPERTIES_FILE") && !(-r "$BASE_SCENARIO_PROPERTIES_FILE") ]]; then
    echo "FATAL: Base scenario properties file does not exist or is not readable."
    exit 1
fi

NEW_SCENARIO_DIR="$SWAC_WORK/scenarios/$NEW_SCENARIO_NAME"
NEW_SCENARIO_XML_FILE="$NEW_SCENARIO_DIR/$NEW_SCENARIO_NAME.xml"
NEW_SCENARIO_PROPERTIES_FILE="$NEW_SCENARIO_DIR/$NEW_SCENARIO_NAME.properties"

# Verify new scenario name is not in use.
if [ -d "$NEW_SCENARIO_DIR" ]; then
    echo "FATAL: Scenario name is already in use."
    exit 1
fi

# Create scenario sub-directories
create_directory "log"
create_directory "outputs"
create_directory "reports"
create_directory "temp"
create_directory "db"
create_directory "cache"

# Copy the required scenario files
copy_file "$BASE_SCENARIO_XML_FILE" "$NEW_SCENARIO_XML_FILE"
copy_file "$BASE_SCENARIO_PROPERTIES_FILE" "$NEW_SCENARIO_PROPERTIES_FILE"

# copy over log4j config files.
for file in `eval ls -1 \"$BASE_SCENARIO_DIR\"  2>/dev/null`; do
    if [[($file == log4j-*.xml)]]
    then
	copy_file "$BASE_SCENARIO_DIR/$file" "$NEW_SCENARIO_DIR"
    fi
done

# file substitution of log4j config files.
for file in `eval ls -1 \"${NEW_SCENARIO_DIR}\"  2>/dev/null`; do
    if [[($file == log4j-*.xml)]]
    then
	file_substitution "${NEW_SCENARIO_DIR}/${file}" "\${swac.work.dir}/scenarios/${BASE_SCENARIO_NAME}" "\${swac.work.dir}/scenarios/${NEW_SCENARIO_NAME}"
    fi
done

copy_file "$BASE_SCENARIO_DIR/itineraryView.sql" "$NEW_SCENARIO_DIR"

# Check if scenario imports file exists and is readable
if [[ (-e "$BASE_SCENARIO_DIR/scenarioImports.csv") && (-r "$BASE_SCENARIO_DIR/scenarioImports.csv") ]]; then
	copy_file "$BASE_SCENARIO_DIR/scenarioImports.csv" "$NEW_SCENARIO_DIR"
fi
