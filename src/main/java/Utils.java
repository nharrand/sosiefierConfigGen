import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Created by nharrand on 18/01/17.
 */
public class Utils {

    public static String getTemplateByID(String template_id) {
        InputStream input = Utils.class.getClassLoader().getResourceAsStream(template_id);
        String result = null;
        try {
            if (input != null) {
                result = org.apache.commons.io.IOUtils.toString(input, java.nio.charset.Charset.forName("UTF-8"));
                input.close();
            } else {
                System.out.println("[Error] Template not found: " + template_id);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return null; // the template was not found
        }
        return result;
    }

    public static void writeFile(File output, String content) {
        try {
            PrintWriter w = new PrintWriter(output);
            w.print(content);
            w.close();
        } catch (Exception ex) {
            System.err.println("Problem writing config file");
            ex.printStackTrace();
        }
    }

    public static String getPathFrom(File f, String s) {
        return f.getAbsolutePath().split(s + "/")[1];
    }
}
