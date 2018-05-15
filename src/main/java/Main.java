import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by nharrand on 05/01/17.
 */
public class Main {
    public static void main(String[] args) {
        //File workingDir = new File("/home/nharrand/Documents/igrida_jobs/sosiefier");


        File workingDir = new File("/home/nharrand/Documents/sosie");
        genConfigProperties(workingDir);


        //File workingDir = new File("/home/nharrand/Documents/sosie/sosiefier");
        //genXploration(workingDir);


        //File workingDir = new File("/home/nharrand/Documents/igrida_jobs/sosiefier");
        //genOarScript(workingDir);

        //File workingDir = new File("/home/nharrand/Documents/sosie/sosiefier/newExp2");
        //generateOtherExp(workingDir);

        //File workingDir = new File("/home/nharrand/Documents/sosie/sosiefier/configurationFiles/result");
        //generateReadResults(workingDir);

        /*File workingDir = new File("/home/nharrand/Documents/sosie/sosiefier/result_nicolas");
        generateBilan(workingDir);*/

        /*File workingDir = new File("/home/nharrand/Documents/sosie/sosiefier");
        genExploration(workingDir);*/
    }

    public static void generateBilan(File workingDir) {

        List<String> transformations = new LinkedList<String>();
        transformations.add("nonstatic_external");
        transformations.add("nonstatic_internal");
        transformations.add("static_external");
        transformations.add("static_internal");
        transformations.add("loopflip");
        transformations.add("swapsubtype");
        transformations.add("delmi");

        List<String> transformationsAddMI = new LinkedList<String>();
        transformationsAddMI.add("nonstatic_external");
        transformationsAddMI.add("nonstatic_internal");
        transformationsAddMI.add("static_external");
        transformationsAddMI.add("static_internal");

        List<String> projects = new LinkedList<String>();
        projects.add("commons-codec");
        projects.add("commons-collections");
        projects.add("commons-io");
        projects.add("commons-lang");
        projects.add("gson");
        projects.add("jgit");

        Bilan b = new Bilan(workingDir,projects,transformations,transformationsAddMI);
        b.generateBilan(new File(workingDir, "Bilan.md"));
    }

    public static void generateReadResults(File workingDir) {

        List<String> transformations = new LinkedList<String>();
        /*transformations.add("nonstatic_external");
        transformations.add("nonstatic_internal");
        transformations.add("static_internal");
        transformations.add("static_external");
        transformations.add("loopflip");
        transformations.add("swapsubtype");*/
        transformations.add("delmi");

        Map<String,String> projects = new HashMap<String,String>();
        projects.put("commons-lang","6");
        projects.put("commons-codec","6");
        projects.put("commons-collections","5");
        projects.put("commons-io","6");
        //projects.put("gson","5");
        //projects.put("jgit","6");

        List<String> configs = new LinkedList<String>();
        String properties;
        for(String exp : transformations) {
            for(String project : projects.keySet()) {
                properties = Utils.getTemplateByID("result.properties");
                properties = properties.replace("#Project#", project);
                if(project.equals("gson"))
                    properties = properties.replace("#ProjectDir#", project + "/" + project);
                else
                    properties = properties.replace("#ProjectDir#", project);
                properties = properties.replace("#javaVersion#", projects.get(project));
                properties = properties.replace("#transfo#", exp);

                if(project.equals("jgit")) {
                    properties += "\n" +
                            "src=org.eclipse.jgit/src\n";
                }

                File runFile = new File(workingDir, "" + project + "_" + exp + ".properties");
                Utils.writeFile(runFile, properties);
                configs.add("configurationFiles/result/" + project + "_" + exp + ".properties");
            }
        }


        String run = "#!/bin/sh\n"
                + "\n";
        for(String c : configs) {
            String tmp = c.replace("/", "_");
            String tmp2 = tmp.replace(".properties", "");
            run += "echo \"Run on " + c + "\"\n";
            run += "java -cp main/target/main-1.0.0-jar-with-dependencies.jar fr.inria.diversify.Main "
                    + c + " > log/" + tmp2 + "\n";
        }
        File runFile = new File(workingDir, "run.sh");
        Utils.writeFile(runFile, run);
    }

    public static void generateOtherExp(File workingDir) {
        List<String> configs = new LinkedList<String>();

        List<String> transformations = new LinkedList<String>();
        transformations.add("loopflip");
        transformations.add("swapsubtype");
        transformations.add("removecheck");

        Map<String,String> projects = new HashMap<String,String>();
        /*projects.put("commons-lang","6");
        projects.put("commons-codec","6");
        projects.put("commons-collections","5");
        projects.put("commons-io","6");*/
        projects.put("gson","5");
        projects.put("jgit","6");

        for(String p : projects.keySet()) {
            for (String t : transformations) {
                OtherTransformations o = new OtherTransformations(t, p, projects.get(p), workingDir);
                configs.add(o.generate());
            }
        }

        String run = "#!/bin/sh\n"
                + "\n"
                + "SOSIE_HOME=\"/home/lyadis/sosie/sosiefier\"\n"
                + "\n"
                + "cd $SOSIE_HOME"
                + "\n";
        for(String c : configs) {
            String tmp = c.replace("/", "_");
            String tmp2 = tmp.replace(".properties", "");
            run += "echo \"Run on newExp/" + c + "\"\n";
            run += "java -cp main/target/main-1.0.0-jar-with-dependencies.jar fr.inria.diversify.Main newExp/"
                    + c + " > newExp/log/" + tmp2 + "\n";
        }
        File runFile = new File(workingDir, "run.sh");
        Utils.writeFile(runFile, run);
    }

    public static void genOarScript(File workingDir) {
        File expDir = new File(workingDir, "script");
        if(!expDir.exists()) {
            expDir.mkdir();
        }
        File transDir = new File(workingDir, "");

        List<JobConfig> configs = new LinkedList<JobConfig>();

        List<String> projects = new LinkedList<String>();

        projects.add("commons-lang");
        projects.add("commons-codec");
        projects.add("commons-collections");
        projects.add("commons-io");
        projects.add("gson");
        String tr = "transformationsToExplore";

        for(String p : projects) {
            //configs.add(new JobConfig(p,new File(workingDir, tr  + "/nonstatic_external/" + p), workingDir));
            //configs.add(new JobConfig(p,new File(workingDir, tr  + "/static_external/" + p), workingDir));
            //configs.add(new JobConfig(p,new File(workingDir, tr  + "/nonstatic_internal/" + p), workingDir));
            //configs.add(new JobConfig(p,new File(workingDir, tr  + "/static_internal/" + p), workingDir));
            configs.add(new JobConfig(p,new File(workingDir, tr  + "/delMI/" + p), workingDir));
        }

        int k = 0;
        for(JobConfig a : configs) {
            int t;
            t = a.generate(expDir, k);
            k=t;
        }

    }

    public static void genXploration(File workingDir) {
        File expDir = new File(workingDir, "Exploration6");
        if(!expDir.exists()) {
            expDir.mkdir();
        }

        List<AddMIConfig> configs = new LinkedList<AddMIConfig>();

        List<String> projects = new LinkedList<String>();

        projects.add("commons-codec");
        projects.add("commons-collections");
        projects.add("commons-io");
        projects.add("commons-lang");
        projects.add("gson");
        projects.add("jgit");

        for(String p : projects) {
            configs.add(new AddMIConfig(p,true,true));
            configs.add(new AddMIConfig(p,true,false));
            configs.add(new AddMIConfig(p,false,true));
            configs.add(new AddMIConfig(p,false,false));
        }

        String run = "#!/bin/sh\n";
        for(AddMIConfig a : configs) {
            a.generateConfigFile(expDir);

            run += "echo \"Run on newExp/" + a.configFileName + "\"\n";
            run += "java -cp main/target/main-1.0.0-jar-with-dependencies.jar fr.inria.diversify.Main " + expDir.getPath()
                    + "/" + a.configFileName + " > " + expDir.getPath() + "/log/" + a.configFileName.replace(".properties", ".log") + "\n";
        }
        File runFile = new File(workingDir, "runXplo.sh");
        Utils.writeFile(runFile, run);


    }

    public static void genExploration(File workingDir) {
        File expDir = new File(workingDir, "Exploration5");
        if(!expDir.exists()) {
            expDir.mkdir();
        }

        Map<String,String> projects = new HashMap<String,String>();
        projects.put("commons-codec","6");
        projects.put("commons-collections","5");
        projects.put("commons-lang","6");
        projects.put("commons-io","6");
        projects.put("gson","5");
        projects.put("jgit","6");
        String template = Utils.getTemplateByID("xploreDelMI.properties");
        //String template = Utils.getTemplateByID("xploreAddMI.properties");

        String run = "#!/bin/sh\n";
        for(Map.Entry<String,String> project : projects.entrySet()) {
            String config = template;
            if(project.getKey().equals("gson"))
                config = config.replace("#PROJECT_DIR#", project.getKey() + "/" + project.getKey());
            else
                config = config.replace("#PROJECT_DIR#", project.getKey());
            config = config.replace("#PROJECT#", project.getKey());
            config = config.replace("#JAVA_VERSION#", project.getValue());

            if(project.getKey().equals("jgit")) {
                config += "\n" +
                        "src=org.eclipse.jgit/src\n";
            }

            File configFile = new File(expDir, project.getKey() + ".properties");
            Utils.writeFile(configFile, config);

            run += "echo \"Run on Exploration4/" + project.getKey() + ".properties\"\n";
            run += "java -cp main/target/main-1.0.0-jar-with-dependencies.jar fr.inria.diversify.Main " + expDir.getPath()
                    + "/" + project.getKey() + ".properties > " + expDir.getPath() + "/log/" + project.getKey() + ".log" + "\n";
        }

        File runFile = new File(workingDir, "runExploration.sh");
        Utils.writeFile(runFile, run);


    }

    public static void genConfigProperties(File workingDir) {
        File expDir = new File(workingDir, "config");
        if(!expDir.exists()) {
            expDir.mkdir();
        }

        Map<String,String> projects = new HashMap<String,String>();
        projects.put("commons-codec","6");
        projects.put("commons-collections","5");
        projects.put("commons-lang","6");
        projects.put("commons-io","6");
        projects.put("gson/gson","5");
        projects.put("jgit","6");
        Map<String,String> projectsPackage = new HashMap<String,String>();
        projectsPackage.put("commons-codec","org.apache.commons.codec");
        projectsPackage.put("commons-collections","org.apache.commons.collections4");
        projectsPackage.put("commons-lang","org.apache.commons.lang3");
        projectsPackage.put("commons-io","org.apache.commons.io");
        projectsPackage.put("gson/gson","com.google.gson");
        projectsPackage.put("jgit","org.eclipse.jgit");
        String template = Utils.getTemplateByID("config.properties");
        for(Map.Entry<String,String> project : projects.entrySet()) {
            String config = template;
            config = config.replace("#PROJECT#", project.getKey());
            config = config.replace("#JAVA_VERSION#", project.getValue());
            config = config.replace("#PACKAGE#", projectsPackage.get(project.getKey()));

            if(project.getKey().equals("jgit")) {
                config += "\n" +
                        "src=org.eclipse.jgit/src\n";
                config += "\n" +
                        "traceExclude=org.eclipse.jgit.ant," +
                        "org.eclipse.jgit.archive," +
                        "org.eclipse.jgit.console," +
                        "org.eclipse.jgit.http," +
                        "org.eclipse.jgit.java7," +
                        "org.eclipse.jgit.junit," +
                        "org.eclipse.jgit.packaging," +
                        "org.eclipse.jgit.pgm," +
                        "org.eclipse.jgit.test," +
                        "org.eclipse.jgit.ui";
            }
            File projectDir = new File(expDir, project.getKey().split("/")[0]);
            if(!projectDir.exists()) {
                projectDir.mkdir();
            }

            File configFile = new File(projectDir, "properties.properties");
            Utils.writeFile(configFile, config);
        }


    }
}
