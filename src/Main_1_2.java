
public class Main_1_2 {

//    // classe contenente il metodo rocchio
//    static Rocchio rocchio;
//    // abilita o no la variante con il calcolo dei near positive invece dei neg
//    static boolean npos;
//    // abilita o no la variante con l'utilizzo dei babelnet id invece che i
//    // lemmi
//    static boolean babel;
    public static void main(String args[]) {
        boolean print = false;

        RocchioLauncher rl1 = new RocchioLauncher(false, false, print);
        Thread t = new Thread(rl1);
        t.start();

        RocchioLauncher rl2 = new RocchioLauncher(false, true, print);
        Thread t2 = new Thread(rl2);
        t2.start();

        RocchioLauncher rl3 = new RocchioLauncher(true, false, print);
        Thread t3 = new Thread(rl3);
        t3.start();

        RocchioLauncher rl4 = new RocchioLauncher(true, true, print);
        Thread t4 = new Thread(rl4);
        t4.start();

    }

}
