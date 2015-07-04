package main;

/**
 *
 * @author Tobia Giani, Salvo Cirinà
 */
public class Launcher implements Runnable {

    // classe contenente il metodo rocchio
    Rocchio rocchio;
    // abilita o no la variante con il calcolo dei near positive invece dei neg
    boolean npos;
    // abilita o no la variante con l'utilizzo dei babelnet id invece che i
    // lemmi
    boolean babel;
    boolean print;

    public Launcher(boolean babel, boolean npos, boolean print) {
        this.babel = babel;
        this.npos = npos;
        this.print = print;
    }

    @Override
    public void run() {
        final String[] types = {"ambiente", "cinema", "cucina", "economia_finanza",
            "motori", "politica", "salute", "scie_tecnologia",
            "spettacoli", "sport"};
        rocchio = new Rocchio(npos, babel, types, print);
        if (print) {
            System.out.println("leggo tutti i documenti e genero il dizionario");
        }

        rocchio.calcolaTFMatrix();
        if (print) {
            System.out.println("generata la tf_matrix");
        }

        rocchio.calcolaCentroidi(types);
        if (print) {
            System.out.println("terminato il calcolo e la scrittura dei centroidi e del dizionario");
        }

        String path = "src/docs_200/sport_05.txt";
        rocchio.calcolaClassePiuSimile(path);
    }

}
