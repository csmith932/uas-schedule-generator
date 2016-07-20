#Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.

#This computer Software was developed with the sponsorship of the U.S. Government
#under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).

usage() {
    cat << EOF
Usage: `basename $0` [-a [-p port]] [-k] [-l level] [-m size] [-v mode] scenario_name
       `basename $0` [-e]
       `basename $0` [-c|--create] scenario_name base_date forecast_year [classifier]
       `basename $0` [-d|--copy] source_scenario_name target_scenario_name
       `basename $0` [-x|--delete] scenario_name
       `basename $0` [-h]

    -a: attach debugger
    -k: generate KML files
    -l: log level
        specify the log level of swac:
        0 - NONE
        1 - VERBOSE (default)
        2 - DEBUG
    -m: memory
        specify jvm\'s maximum allowed memory in megabytes 
        (size - default 4096).
    -p: specify the port the debugger will ues to connect to the jvm 
        (port - default 4000).
    -v: specify the data validation mode:
        0 - NORMAL: Files are validated against specified schema (Default).
        1 - DEEP: Modules are preloaded to verify their input data loads and 
            additional validation checks may be made to verify the referential 
            integrity of the input data.
        2 - DEEP_NO_EXECUTE: Same as DEEP but all scenarios are checked up front 
            without being executed.
    -e: extract data files
    -c|--create: create scenario 
        calls the swac-create-scenario script 
        (
            scenario_name: name for the new scenario.
            base_date: the base date(s) to use for the new scenario.
            forecast_year: the fiscal forecast year(s) for the scenario.
            classifier: (optional) the classifier(s) for the scenario.
        )
    -d|--copy: copy a scenario
        calls the swac-copy-scenario script
        (
            source_scenario_name: the name of the scenario to copy.
            target_scenario_name: the name of the new scenario created.
        )
    -x|--delete: delete a scenario
        calls the swac-delete-scenario script
        (
            scenario_name: the name of the scenario to delete.
        )
    -h: help
        outputs this usage statement.
EOF
    exit $1
}

JVM_MAX_SIZE=4096m

LIB_JAVA="${SWAC_HOME}/lib/java"


if [ -w "$SWAC_WORK/log" ]; then
    rm -rf "$SWAC_WORK/log/*"
fi

attach_debugger=false
data_extraction=false
debugger_port=4000
gen_kml="FALSE"
log_level="VERBOSE"
validation_level="NORMAL"

while getopts "hacdekl:m:p:v:xw:-:" OPTION; do
    case $OPTION in
        a) 
            attach_debugger=true
            ;;
        c)
            shift `expr $OPTIND - 1`
            exec swac-create-scenario $@
            exit $?
            ;;
        d)
            shift `expr $OPTIND - 1`
            exec swac-copy-scenario $@
            exit $?
            ;;
        e)
            data_extraction=true
            ;;
        k)
            gen_kml="TRUE"
            ;;
        l)
            case $OPTARG in
                0) log_level="NONE" ;;
                2) log_level="DEBUG" ;;
                *) log_level="VERBOSE" ;;
            esac
            ;;
        m)
            JVM_MAX_SIZE="${OPTARG}m"
            ;;
        p) 
            debugger_port=$OPTARG
            ;;
        v)
            case $OPTARG in
                1) validation_level="DEEP" ;;
                2) validation_level="DEEP_NO_EXECUTE" ;;
                *) validation_level="NORMAL" ;;
            esac
            ;;
        x)
            shift `expr $OPTIND - 1`
            exec swac-delete-scenario $@
            exit $?
            ;;
        w)
            SWAC_WORK=$OPTARG
            ;;
        -)
            case $OPTARG in
                create)
                    shift `expr $OPTIND - 1`
                    exec swac-create-scenario $@
                    exit $?
                    ;;
                copy)
                    shift `expr $OPTIND - 1`
                    exec swac-copy-scenario $@
                    exit $?
                    ;;
                delete)
                    shift `expr $OPTIND - 1`
                    exec swac-delete-scenario $@
                    exit $?
                    ;;
            esac;;
        h)
            usage 0
            ;;
        *)
            usage 1
            ;;
    esac
done
if [[ $# > 1 ]]; then
    shift `expr $OPTIND - 1 2>/dev/null` # Suppress error output.
fi

if [[ $data_extraction == false ]]; then
    if [[ $# < 1 ]]; then
        echo "Scenario name must be specified!"
        usage 1
    fi
elif [ -e "$LIB_JAVA/swac-data-*-SNAPSHOT.jar" ]; then
    echo "Unable to locate data jar file for extraction!"
    echo "Please ensure the data jar file is located in ${LIB_JAVA}"
    exit 1
fi

if [ $attach_debugger == true ]; then
    java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=$debugger_port,suspend=y -Xms256m -Xmx${JVM_MAX_SIZE} -XX:+UseParallelGC -Dswac.work.dir="$SWAC_WORK" -Dswac.data.extraction=${data_extraction} -Dswac.log.level=${log_level} -Dswac.gen.kml=${gen_kml} -Dswac.validation.level=$validation_level -cp "${LIB_JAVA}/"\* gov.faa.ang.swac.controller.Bootstrap $@
else
    java -Xms256m -Xmx${JVM_MAX_SIZE} -XX:+UseParallelGC -Dswac.work.dir="$SWAC_WORK" -Dswac.data.extraction=${data_extraction} -Dswac.log.level=${log_level} -Dswac.gen.kml=${gen_kml} -Dswac.validation.level=$validation_level -cp "${LIB_JAVA}/"\* gov.faa.ang.swac.controller.Bootstrap $@
fi

exit $?
