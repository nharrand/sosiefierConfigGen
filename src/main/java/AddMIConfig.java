import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Created by nharrand on 10/01/17.
 */
public class AddMIConfig {
    String project;
    boolean isStatic;
    boolean isIntern;
    String configFileName;

    public AddMIConfig(String project, boolean isStatic, boolean isIntern) {
        this.project = project;
        this.isIntern = isIntern;
        this.isStatic = isStatic;
    }

    public String generate() {
        String config = Utils.getTemplateByID("xploreAddMI.properties");
        config = config.replace("#PROJECT#", project);
        if(project.equals("gson")) {
            config = config.replace("#PROJECTPATH#", project + "/" + project);
        } else {
            config = config.replace("#PROJECTPATH#", project);
        }

        if(project.equals("jgit")) {
            config += "\n" +
                    "src=org.eclipse.jgit/src\n";
        }


        config = config.replace("#INTERNAL#", "" + isIntern);
        config = config.replace("#EXTTERNAL#", "" + !isIntern);
        config = config.replace("#STATIC#", "" + isStatic);
        config = config.replace("#NON_STATIC#", "" + !isStatic);
        config = config.replace("#dumpMethodsAfterSuccess#", "" + (isStatic && !isIntern));
        config = config.replace("#shuffleCandidate#", "false");
        config = config.replace("#shuffleMethods#", "" + (isStatic && !isIntern));
        config = config.replace("#maxMethodTryPerStmt#", "" + ((isStatic && !isIntern) ? 1 : 100));

        String subfield = "";
        if(isStatic) subfield += "static";
        else subfield += "nonstatic";
        subfield += "_";
        if(isIntern) subfield += "internal";
        else subfield += "external";

        config = config.replace("#SUBFIELD#", subfield);

        configFileName = subfield + "_" + project + ".properties";
        return config;
    }

    public void generateConfigFile(File output) {
        String conf = generate();
        File configFile = new File(output, configFileName);
        Utils.writeFile(configFile,conf);
    }
}
