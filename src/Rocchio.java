
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Rocchio {

    Dizionario dict;
    // array contenente tutte le parole incontrate nei 200 documenti
    String[] parole;
    // abilita o no la variante con il calcolo dei near positive invece dei neg
    boolean npos;
    // abilita o no la variante con l'utilizzo dei babelnet id invece che i
    // lemmi
    boolean babel;
    boolean print;
    double[][] tf_matrix;

    /*
     * parte relativa al main_1_2
     */
    public Rocchio(boolean npos_flag, boolean babel_flag, String[] classi,
            Dizionario.Lang language, boolean print) {
        npos = npos_flag;
        babel = babel_flag;
        dict = new Dizionario(babel, language, print);
        dict.generaDizionarioDaListaType(classi);
        this.print = print;
    }

    /*
     * parte relativa al main_3
     */
    public Rocchio(boolean npos_flag, boolean babel_flag,
            Dizionario.Lang language, boolean print) {
        npos = npos_flag;
        babel = babel_flag;
        dict = new Dizionario(babel, language, print);
        this.print = print;
    }

    /*
     * parte relativa al main_1_2
     */
    public void calcolaTFMatrix() {
        parole = dict.getDizionario();
        tf_matrix = new double[200][parole.length];

        for (int i = 0; i < tf_matrix.length; i++) {
            for (int j = 0; j < tf_matrix[0].length; j++) {
                int[] docs = dict.getOccorrenze(parole[j]);
                double count = 0;
                if (docs != null) {
                    for (int doc : docs) {
                        if (doc != 0) {
                            count++;
                        }
                    }
                }
                tf_matrix[i][j] = ((double) docs[i]) * Math.log(200 / count);
            }
        }
    }

    public void writeTFMatrix() {
        String text = "";
        for (int i = 0; i < tf_matrix.length; i++) {
            for (int j = 0; j < tf_matrix[0].length; j++) {
                text += tf_matrix[i][j]
                        + (j != tf_matrix[0].length - 1 ? "," : "");

            }
            text += "\n";
            if (print) {
                System.out.println("scritto " + i + " di 200");
            }
        }
        try {
            String ext = "";
            if (npos) {
                ext += "_npos";
            } else {
                ext += "_nonpos";
            }

            if (babel) {
                ext += "_babel";
            } else {
                ext += "_nobabel";
            }
            Files.write(Paths.get("tfmatrix" + ext + ".txt"), text.getBytes());
            if (print) {
                System.out.println("scrittura terminata");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public double calcolaCosSimilarity(double[] array1, double[] array2) {
        double product = 0;
        if (array1.length == array2.length) {
            double norma_2 = 0;
            double norma_1 = 0;

            for (int i = 0; i < array1.length; i++) {
                norma_2 += array2[i] * array2[i];
                norma_1 += array1[i] * array1[i];
                product += array1[i] * array2[i];
            }
            product /= Math.sqrt(norma_2 * norma_1);
        } else {
            if (print) {
                System.out
                        .println("I due vettori non sono della stessa dimensione");
            }
        }
        return product;

    }

    public void calcolaCentroidi(String types[]) {

        int beta = 16;
        int gamma = 4;
        double[][] centroids = new double[types.length][parole.length];
        String text = "";
        for (int i = 0; i < types.length; i++) {
            double[] vettore_centr = new double[parole.length];
            for (int j = i * 20; j < (i + 1) * 20; j++) {
                for (int k = 0; k < parole.length; k++) {
                    vettore_centr[k] += tf_matrix[j][k] / 20;
                    centroids[i][k] += beta * tf_matrix[j][k] / 20;
                }
            }
            if (print) {
                System.out.print("centroide num " + i + " ");
            }
            for (int j = 0; j < i * 20; j++) {
                double sim = 0;
                if (npos) {
                    sim = calcolaCosSimilarity(vettore_centr, tf_matrix[j]);
                }

                for (int k = 0; k < parole.length; k++) {
                    if (npos) {
                        if (print) {
                            System.out.print(sim + " : ");
                        }

                        if (sim > 0.8) {
                            centroids[i][k] -= gamma * tf_matrix[j][k] / 180;
                        }
                    } else {
                        centroids[i][k] -= gamma * tf_matrix[j][k] / 180;
                    }
                }
            }

            for (int j = (i + 1) * 20; j < 200; j++) {
                double sim = calcolaCosSimilarity(vettore_centr, tf_matrix[j]);

                for (int k = 0; k < parole.length; k++) {
                    if (npos) {
                        if (sim > 0.8) {
                            centroids[i][k] -= gamma * tf_matrix[j][k] / 180;
                        }
                    } else {
                        centroids[i][k] -= gamma * tf_matrix[j][k] / 180;
                    }
                }

            }
            if (print) {
                System.out.println();
            }

            for (int k = 0; k < parole.length; k++) {
                text += centroids[i][k] + (k != parole.length - 1 ? "," : "");
            }
            text += "\n";
        }

        String par = "";
        for (String p : parole) {
            par += p + "\n";
        }

        try {
            String ext = "";
            if (npos) {
                ext += "_npos";
            } else {
                ext += "_nonpos";
            }

            if (babel) {
                ext += "_babel";
            } else {
                ext += "_nobabel";
            }

            Files.write(Paths.get("dizionario" + ext + ".txt"), par.getBytes());
            Files.write(Paths.get("centroids" + ext + ".txt"), text.getBytes());

        } catch (IOException ex) {
            Logger.getLogger(Main_1_2.class
                    .getName()).log(Level.SEVERE, null,
                            ex);
        }
    }

    /*
     * parte relativa al main_3
     */
    public double[][] leggiTFMatrice() {
        double[][] tf_matrice;
        try {
            String ext = "";
            if (npos) {
                ext += "_npos";
            } else {
                ext += "_nonpos";
            }

            if (babel) {
                ext += "_babel";
            } else {
                ext += "_nobabel";
            }
            List<String> linee = Files.readAllLines(Paths.get("centroids" + ext + ".txt"),
                    Charset.defaultCharset());
            int num_classi = linee.size();
            String[] riga = linee.get(0).split(",");
            int num_parole = riga.length;
            tf_matrice = new double[num_classi][num_parole];
            for (int i = 0; i < num_classi; i++) {
                riga = linee.get(i).split(",");
                for (int j = 0; j < num_parole; j++) {
                    tf_matrice[i][j] = Double.parseDouble(riga[j]);
                }
            }

            ArrayList<String> linee_diz = (ArrayList<String>) Files
                    .readAllLines(Paths.get("dizionario" + ext + ".txt"),
                            Charset.defaultCharset());
            for (String linea : linee_diz) {
                int[] x = new int[1];
                x[0] = 0;
                dict.addToDizionario(linea, x);
            }
            return tf_matrice;

        } catch (IOException ex) {
            Logger.getLogger(Main_3.class
                    .getName())
                    .log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double[] calcolaTFVettore(String path) {
        double[] tf_vettore;
        dict.generaDizionarioFilePathIT(path);
        parole = dict.getDizionario();

        tf_vettore = new double[parole.length];
        for (int j = 0; j < tf_vettore.length; j++) {
            tf_vettore[j] = dict.getOccorrenze(parole[j])[0];
        }
        return tf_vettore;
    }

    public String[] getParolePiuComuni(int index) {
        boolean passoBase = true;
        String[] temp = dict.getDizionario();
        String[] words = new String[3];
        int[] occorrenze = new int[3];
        for (String temp1 : temp) {
            int occorrenza = dict.getOccorrenze(temp1)[index];
            // passo base
            if (passoBase) {
                words[0] = temp1;
                occorrenze[0] = occorrenza;
                passoBase = false;
            }
            if (occorrenze[0] <= occorrenza) {
                occorrenze[2] = occorrenze[1];
                words[2] = words[1];
                occorrenze[1] = occorrenze[0];
                words[1] = words[0];
                occorrenze[0] = occorrenza;
                words[0] = temp1;
            } else if (occorrenze[1] <= occorrenza) {
                occorrenze[2] = occorrenze[1];
                words[2] = words[1];
                occorrenze[1] = occorrenza;
                words[1] = temp1;
            } else if (occorrenze[2] <= occorrenza) {
                occorrenze[2] = occorrenza;
                words[2] = temp1;
            }
        }
        return words;
    }

}
