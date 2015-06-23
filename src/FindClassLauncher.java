
/**
 *
 * @author Tobia Giani, Salvo Cirinà
 */
public class FindClassLauncher implements Runnable {

    // classe contenente il metodo rocchio
    Rocchio rocchio;
    // abilita o no la variante con il calcolo dei near positive invece dei neg
    boolean npos;
    // abilita o no la variante con l'utilizzo dei babelnet id invece che i
    // lemmi
    boolean babel;
    boolean print;

    public FindClassLauncher(boolean babel, boolean npos, boolean print) {
        this.babel = babel;
        this.npos = npos;
        this.print = print;
    }

    @Override
    public void run() {
        rocchio = new Rocchio(npos, babel, Dizionario.Lang.IT, print);
        rocchio.leggiTFMatrice();
        String path = "src/docs_200/sport_05.txt";
        rocchio.calcolaClassePiuSimile(path);
    }

}
