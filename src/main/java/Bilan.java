import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

/**
 * Created by nharrand on 01/06/17.
 */
public class Bilan {
    public static String transFile = ".json";
    public static String sosieFile = "_sosie.json";

    public File srcDir;
    public List<String> project;
    public List<String> exp;
    public List<String> expAddMI;
    public List<Cas> cas = new ArrayList<Cas>();


    public Bilan(File srcDir, List<String> project, List<String> exp) {
        this.srcDir = srcDir;
        this.project = project;
        this.exp = exp;
        this.expAddMI = new LinkedList<String>();
    }

    public Bilan(File srcDir, List<String> project, List<String> exp, List<String> expAddMI) {
        this.srcDir = srcDir;
        this.project = project;
        this.exp = exp;
        this.expAddMI = expAddMI;
    }

    public void generateBilan(File output) {
        String templateContent = Utils.getTemplateByID("Bilan.md");
        String res = "";

        int projectTransAddMI[] = new int[project.size()];
        int projectSosieAddMI[] = new int[project.size()];
        int projectTrans[] = new int[project.size()];
        int projectSosie[] = new int[project.size()];
        int expTrans[] = new int[exp.size()];
        int expSosie[] = new int[exp.size()];

        int i = 0;
        for(String p : project) {
            int j = 0;
            for(String e : exp) {
                Cas c = new Cas(p,e);
                res += "| " + p + " | " + e + " | " + c.nbTrans + " | " + c.nbSosie + " | " + c.ratio + " |\n";
                projectTrans[i] += c.nbTrans;
                projectSosie[i] += c.nbSosie;
                expTrans[j] += c.nbTrans;
                expSosie[j] += c.nbSosie;

                if(isIn(expAddMI, e)) {
                    projectTransAddMI[i] += c.nbTrans;
                    projectSosieAddMI[i] += c.nbSosie;
                }
                j++;
            }
            i++;
        }

        String projetBilan = "| Project | nb Transformations | nb Sosies | ratio |\n";
        projetBilan += "| ------ | ------------------ | --------- | ----- |\n";
        for(int k = 0; k < project.size(); k++) {
            projetBilan += "| " + project.get(k) + " | " + projectTrans[k] + " | " + projectSosie[k] + " | " + (((double) projectSosie[k]) / ((double) projectTrans[k])) + " |\n";
        }

        String projetBilanAddMI = "| Project(AddMI) | nb Transformations | nb Sosies | ratio |\n";
        projetBilanAddMI += "| ------ | ------------------ | --------- | ----- |\n";
        for(int k = 0; k < project.size(); k++) {
            projetBilanAddMI += "| " + project.get(k) + " | " + projectTransAddMI[k] + " | " + projectSosieAddMI[k] + " | " + (((double) projectSosieAddMI[k]) / ((double) projectTransAddMI[k])) + " |\n";
        }

        projetBilanAddMI += "\n\n```";
        projetBilanAddMI += " & nb Transformations & nb Sosies & ratio \\\\\n";
        projetBilanAddMI += "\\hline\n";
        int totTrial= 0;
        int totSosie = 0;
        for(int k = 0; k < project.size(); k++) {
            projetBilanAddMI += " " + project.get(k) + " & " + projectTransAddMI[k] + " & " + projectSosieAddMI[k] + " & " + (((double) projectSosieAddMI[k]) / ((double) projectTransAddMI[k])) + " \\\\\n";
            totTrial += projectTransAddMI[k];
            totSosie += projectSosieAddMI[k];
        }
        projetBilanAddMI += " total & " + totTrial + " & " + totSosie + " & " + ((double) totSosie / (double) totTrial) + "\n";
        projetBilanAddMI += "\\hline\n```\n";

        String expBilan = "| Experience | nb Transformations | nb Sosies | ratio |\n";
        expBilan += "| ---------- | ------------------ | --------- | ----- |\n";
        for(int l = 0; l < exp.size(); l++) {
            expBilan += "| " + exp.get(l) + " | " + expTrans[l] + " | " + expSosie[l] + " | " + (((double) expSosie[l]) / ((double) expTrans[l])) + " |\n";
        }

        templateContent = templateContent.replace("#INSERT#", res + "\n" + expBilan + "\n" + projetBilan + "\n" + projetBilanAddMI);
        Utils.writeFile(output, templateContent);
    }



    class Cas {
        public int nbTrans = 0;
        public int nbSosie = 0;
        public double ratio = 0;

        public File transFile;
        public File sosieFile;

        public Cas(String projet, String exp) {
            transFile = new File(srcDir, projet + "_" + exp + Bilan.transFile);
            sosieFile = new File(srcDir, projet + "_" + exp + Bilan.sosieFile);

            try {
                if(transFile.exists() && sosieFile.exists()) {
                    nbTrans = getNbTransformations(readFromFile(transFile));
                    nbSosie = getNbTransformations(readFromFile(sosieFile));
                    ratio = ((double) nbSosie) / ((double) nbTrans);
                }
            } catch(JSONException e) {

            }
        }


        public int getNbTransformations(JSONObject o) throws JSONException {
            return o.getJSONObject("header").getInt("transformationCount");
        }

        public JSONObject readFromFile(File f) {
            JSONObject jsonObject = null;
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(f));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }

                jsonObject = new JSONObject(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (br != null) try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return jsonObject;
        }
    }

    static boolean isIn(Collection c, Object el) {
        for (Object o : c) {if(o.equals(el)) return true;}
        return false;
    }
}
