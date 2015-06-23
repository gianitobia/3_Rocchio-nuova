
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tobia Giani, Salvo Cirinà
 */
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
    double[][] centroids;

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

        for (int j = 0; j < parole.length; j++) {
            int[] docs = dict.getOccorrenze(parole[j]);
            double count = 0;
            for (int doc : docs) {
                if (doc != 0) {
                    count++;
                }
            }

            for (int i = 0; i < tf_matrix.length; i++) {
                if (count > 0) {
                    tf_matrix[i][j] = ((double) docs[i]) * Math.log(200 / count);
                } else {
                    tf_matrix[i][j] = 0;
                }
            }

        }
    }

    public void writeTFMatrix() {
        String text = "";
        for (int i = 0; i < tf_matrix.length; i++) {
            for (int j = 0; j < tf_matrix[0].length; j++) {
                text += tf_matrix[i][j]
                        + (j != tf_matrix[0].length - 1 ? "," : "\n");

            }

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
            product /= Math.sqrt(norma_2) * Math.sqrt(norma_1);
        } else {
            if (print) {
                System.out.println("I due vettori non sono della stessa dimensione");
            }
        }
        return product;

    }

    public void calcolaCentroidi(String types[]) {

        int beta = 16;
        int gamma = 4;
        centroids = new double[types.length][parole.length];
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

            for (int k = 0; k < parole.length; k++) {
                text += centroids[i][k] + (k != parole.length - 1 ? "," : "\n");
            }
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
    public void leggiTFMatrice() {
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
            centroids = new double[num_classi][num_parole];
            for (int i = 0; i < num_classi; i++) {
                riga = linee.get(i).split(",");
                for (int j = 0; j < num_parole; j++) {
                    centroids[i][j] = Double.parseDouble(riga[j]);
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
        } catch (IOException ex) {
            Logger.getLogger(Main_3.class
                    .getName())
                    .log(Level.SEVERE, null, ex);
        }
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

    void calcolaClassePiuSimile(String path) {
        double[] tf_vettore = calcolaTFVettore(path);

        String[] types = {"ambiente", "cinema", "cucina", "economia_finanza",
            "motori", "politica", "salute", "scie_tecnologia",
            "spettacoli", "sport"};

        double largest = 0, sim;
        int index = 0;
        for (int i = 0; i < centroids.length; i++) {
            sim = calcolaCosSimilarity(centroids[i], tf_vettore);
            if (sim > largest) {
                largest = sim;
                index = i;
            }
        }

        System.out.println("L'articolo fa parte della classe " + types[index]
                + " con similarita' pari a " + largest);
    }

}
