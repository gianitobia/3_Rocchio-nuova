/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author toby,ifero
 */
public class Main_3 {

    static double[] sim;
    static Rocchio rocchio;
    static double[][] tf_matrice;
    static double[] tf_vettore;
    // abilita o no la variante con l'utilizzo dei babelnet id invece che i
    // lemmi
    static boolean babel;
    static boolean npos;
    static boolean print;

    public static void main(String args[]) {
        babel = false;
        npos = false;
        print = true;
        rocchio = new Rocchio(npos, babel, Dizionario.Lang.IT, print);
        tf_matrice = rocchio.leggiTFMatrice();
        tf_vettore = rocchio.calcolaTFVettore("src/docs_200/sport_05.txt");

        String[] types = {"ambiente", "cinema", "cucina", "economia_finanza",
            "motori", "politica", "salute", "scie_tecnologia",
            "spettacoli", "sport"};

        sim = new double[types.length];
        calcolaCosSimilarity();

        double largest = sim[0];
        int index = 0;
        for (int i = 1; i < sim.length; i++) {
            if (sim[i] > largest) {
                largest = sim[i];
                index = i;
            }
        }

        if (print) {
            System.out.println("L'articolo fa parte della classe " + types[index]
                    + " con similarita' pari a " + largest);
        }
    }

    static void calcolaCosSimilarity() {
        if (tf_matrice[0].length == tf_vettore.length) {
            for (int i = 0; i < tf_matrice.length; i++) {
                sim[i] = rocchio
                        .calcolaCosSimilarity(tf_matrice[i], tf_vettore);
            }
        } else {
            if (print) {
                System.out
                        .println("Il documento non contiene lo stesso dizionario del database di documenti");
            }
        }
    }
}
