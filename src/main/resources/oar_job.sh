#!/bin/sh
#OAR -l core=1,walltime=02:00:00
#OAR --array-param-file /temp_dd/igrida-fs1/nharrand/igrida_jobs/sosiefier/script/param_#ID#.txt
#OAR -O /temp_dd/igrida-fs1/nharrand/igrida_jobs/sosiefier/log/job#ID#.%jobid%.output
#OAR -E /temp_dd/igrida-fs1/nharrand/igrida_jobs/sosiefier/log/job#ID#.%jobid%.error
set -xv


echo
echo "=============== RUN ==============="
echo "Running ... $USER at `cat /etc/hostname`"
/temp_dd/igrida-fs1/nharrand/igrida_jobs/sosiefier/gen_config.sh $*
echo "Done"
echo "==================================="
