import javax.rmi.CORBA.Util;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by nharrand on 18/01/17.
 */
public class JobConfig {
    public String projectName;
    public File transDir;
    public int groupSize = 10;
    public File template;
    public File workingDir;

    public JobConfig(String projectName, File transDir, File workingDir) {
        this.projectName = projectName;
        this.transDir = transDir;
        this.workingDir = workingDir;
    }

    public int generate(File scriptDir, int k) {
        String templateContent = Utils.getTemplateByID("execTrans.properties");
        templateContent = templateContent.replace("#Project#", projectName);
        template = new File(scriptDir, "template_" + projectName + ".properties");
        Utils.writeFile(template, templateContent);

        File[] jsons = transDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if(name.endsWith(".json")) return true;
                else return false;
            }
        });

        if(!transDir.exists()) {
            System.out.println("transDir doesn't exist: " + transDir.getName());
        } else {

            int i = 0;
            int j;
            String param, script;
            while(i < jsons.length) {
                j = 0;
                param =  Utils.getPathFrom(template, workingDir.getName());
                while((i < jsons.length) && (j < groupSize)) {
                    param += " " + Utils.getPathFrom(jsons[i], workingDir.getName());
                    j++;
                    i++;
                }
                File paramF = new File(scriptDir, "param_" + k + ".txt");
                Utils.writeFile(paramF,param);
                //File scriptF = new File(scriptDir, "job_" + k + ".sh");
                //script = Utils.getTemplateByID("oar_job.sh");
                //script = script.replace("#ID#", k + "");
                //Utils.writeFile(scriptF,script);

                k++;
            }
        }

        return k;

    }


}
