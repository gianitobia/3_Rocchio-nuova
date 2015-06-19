
public class Main_1_2 {

    // classe contenente il metodo rocchio
    static Rocchio rocchio;
    // abilita o no la variante con il calcolo dei near positive invece dei neg
    static boolean npos;
    // abilita o no la variante con l'utilizzo dei babelnet id invece che i
    // lemmi
    static boolean babel;

    public static void main(String args[]) {
        npos = false;
        babel = false;
        String[] types = {"ambiente", "cinema", "cucina", "economia_finanza",
            "motori", "politica", "salute", "scie_tecnologia",
            "spettacoli", "sport"};
        // genero dizionario leggendo i 200 file e passandoli al tokenizer il
        // quale può
        // - o tokenizzarli e lemmatizzarli usando morph_it
        // per far ci�� prima il testo viene suddiviso in linee e poi ogni linea
        // viene tokenizzata, e poi ogni parola trovata viene confrontata con le
        // info date da morph-it per lemmatizzarla
        // - o li tokenizza, li lemmatizza e poi li riduce ai babelid usando
        // babelnet
        rocchio = new Rocchio(npos, babel, types, Dizionario.Lang.IT);
        System.out.println("leggo tutti i documenti e genero il dizionario");
//         double[][] tf_matrix = rocchio.calcolaTFMatrix();
//         System.out.println("genero la tf_matrix");
//         rocchio.writeTFMatrix(tf_matrix);
//         System.out.println("termino la scrittura della tf_matrix");
//         rocchio.calcolaCentroidi(types,tf_matrix);
    }

}
