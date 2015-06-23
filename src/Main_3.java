
/**
 *
 * @author Tobia Giani, Salvo Cirinà
 */
public class Main_3 {

    static Rocchio rocchio;
    // abilita o no la variante con l'utilizzo dei babelnet id invece che i
    // lemmi

    public static void main(String args[]) {
        boolean print = true;

        FindClassLauncher rl1 = new FindClassLauncher(false, false, print);
        Thread t = new Thread(rl1);
        t.start();

        FindClassLauncher rl2 = new FindClassLauncher(false, true, print);
        Thread t2 = new Thread(rl2);
        t2.start();

        FindClassLauncher rl3 = new FindClassLauncher(true, false, print);
        Thread t3 = new Thread(rl3);
        t3.start();

        FindClassLauncher rl4 = new FindClassLauncher(true, true, print);
        Thread t4 = new Thread(rl4);
        t4.start();
    }
}
