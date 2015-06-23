/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author toby
 */
public class Main {

    public static void main(String args[]) {

        boolean print = false;

        Launcher rl1 = new Launcher(false, false, print);
        Thread t = new Thread(rl1);

        t.start();

        Launcher rl2 = new Launcher(false, true, print);
        Thread t2 = new Thread(rl2);

        t2.start();

        Launcher rl3 = new Launcher(true, false, print);
        Thread t3 = new Thread(rl3);

        t3.start();

        Launcher rl4 = new Launcher(true, true, print);
        Thread t4 = new Thread(rl4);

        t4.start();
    }

}
