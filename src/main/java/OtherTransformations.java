import java.io.File;

/**
 * Created by nharrand on 15/02/17.
 */
public class OtherTransformations {
    public String transformation;
    public String projectName;
    public String javaVersion;
    public File configFile;
    public File workingDir;

    public OtherTransformations(String transformation, String projectName, String javaVersion, File workingDir) {

        this.transformation = transformation;
        this.projectName = projectName;
        this.javaVersion = javaVersion;
        this.workingDir = workingDir;
    }

    public String generate() {
        String templateContent = Utils.getTemplateByID("properties.properties");
        templateContent = templateContent.replace("#Project#", projectName);
        templateContent = templateContent.replace("#javaVersion#", javaVersion);
        templateContent = templateContent.replace("#transformation#", transformation);
        templateContent = templateContent.replace("#resultDir#", transformation + "/" + projectName);

        File resultDir = new File(workingDir, "result/" + transformation + "/" + projectName);
        resultDir.mkdirs();

        configFile = new File(workingDir, "config/" + transformation + "_" + projectName + ".properties");
        Utils.writeFile(configFile, templateContent);

        return  "config/" + transformation + "_" + projectName + ".properties";
    }
}
