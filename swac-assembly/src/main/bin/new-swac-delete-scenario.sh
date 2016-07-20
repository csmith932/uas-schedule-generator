#!/bin/bash

#Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.

#This computer Software was developed with the sponsorship of the U.S. Government
#under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).

function usage() {
cat << EOF
Usage: swac-delete-scenario.sh scenario_name [-a [path]]
    Deletes the scenario file, property file, output directory, and report directory for a given scenario.

    The following argument is mandatory:
        scenario_name - The name of the scenario to delete.

    -a: Create archive before deleting scenario. By default the archive will be output as: ${SWAC_WORK}/archives/[scenario_name_yyyymmddHHmmss.tar.gz.
        path - Overrides the default filename and path to write the archive.
EOF
    exit $1
}

if [[ $# < 1 ]]
then
    usage 1 
fi

#$1 - Scenario Name
SCENARIO_NAME=$1

shift 1

archive=false

while getopts "ha" OPTION; do
    case $OPTION in
        a) 
            archive=true
            shift 1
            if [[ $# > 0 ]]; then
                path=$1
            fi
            ;;
        h)
            usage 0
            ;;
        *)
            usage 1
            ;;
    esac
done

SCENARIO_DIR="$SWAC_WORK/scenarios/$SCENARIO_NAME"

if [[ $archive == true ]]; then
    if [ -n "$path" ]; then
        tar zcf $path $SCENARIO_DIR

        errno=$?

        if [[ $errno != 0 ]]; then
            echo "ERROR: Could not create archive: ${path}! tar exited with errno: ${errno}."
            exit $errno
        fi
    else
        if [[ ! -d "$SWAC_WORK/archives" || ! -w "$SWAC_WORK/archives" ]]; then
            mkdir "$SWAC_WORK/archives"
        fi
        
        time=`date +%Y%m%d%H%M%S`
        tar -zcf "$SWAC_WORK/archives/$SCENARIO_NAME_$time.tar.gz" "$SCENARIO_DIR"

        errno=$?

        if [[ $errno != 0 ]]; then
            echo "ERROR: Could not create archive: ${SWAC_WORK}/archives/${SCENARIO_NAME}_${time}.tar.gz! tar exited with errno: ${errno}."
            exit $errno
        fi
    fi
fi

rm -rf "$SCENARIO_DIR" 2>/dev/null

errno=$?

if [[ $errno != 0 ]]; then
    echo "FATAL: Could not delete scenario directory ${SCENARIO_DIR}. rm exited with errno: ${errno}."
    exit $errno
else
    echo "INFO: Scenario directory ${SCENARIO_DIR} deleted."
fi

exit $errno
