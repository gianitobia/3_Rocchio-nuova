
class RocchioLauncher implements Runnable {

    // classe contenente il metodo rocchio
    Rocchio rocchio;
    // abilita o no la variante con il calcolo dei near positive invece dei neg
    boolean npos;
    // abilita o no la variante con l'utilizzo dei babelnet id invece che i
    // lemmi
    boolean babel;
    boolean print;

    public RocchioLauncher(boolean babel, boolean npos, boolean print) {
        this.babel = babel;
        this.npos = npos;
    }

    @Override
    public void run() {
        final String[] types = {"ambiente", "cinema", "cucina", "economia_finanza",
            "motori", "politica", "salute", "scie_tecnologia",
            "spettacoli", "sport"};
        rocchio = new Rocchio(npos, babel, types, Dizionario.Lang.IT, print);
        if (print) {
            System.out.println("leggo tutti i documenti e genero il dizionario");
        }

        rocchio.calcolaTFMatrix();
        if (print) {
            System.out.println("genero la tf_matrix");
        }
        rocchio.writeTFMatrix();
        if (print) {
            System.out.println("termino la scrittura della tf_matrix");
        }
        rocchio.calcolaCentroidi(types);
    }

}
