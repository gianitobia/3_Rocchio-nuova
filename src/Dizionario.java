
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Dizionario {

    static public enum Lang {

        EN, IT
    };

    static HashMap<String, int[]> dizionario;
    static private Tokenizer_IT tokenizerIT;
    static private Tokenizer_EN tokenizerEN;

    // flag per effettuare la tokenizzazione italiana tramite babelnet id's
    // piuttosto che
    // lemmi
    static private boolean babel;

    public Dizionario(boolean flag, Lang language) {
        babel = flag;
        dizionario = new HashMap<>();
        if (language == Lang.IT) {
            tokenizerIT = new Tokenizer_IT(babel);
        } else {
            tokenizerEN = new Tokenizer_EN();
        }
    }

    public void generaDizionarioEN(String path) {
        try {
            ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(
                    Paths.get(path), Charset.forName("CP850"));
            ArrayList<ArrayList<String>> testiAnalizzati = tokenizerEN
                    .analizzaListaTesti(lines);
            for (ArrayList<String> lineaAnalizzata : testiAnalizzati) {
                for (String token : lineaAnalizzata) {
                    if (!"".equals(token)) {
                        if (dizionario.containsKey(token)) {
                            int[] occ = dizionario.get(token);
                            occ[0]++;
                            dizionario.put(token, occ);
                        } else {
                            int[] x = new int[1];
                            x[0] = 1;
                            dizionario.put(token, x);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void generaDizionario(String[] types) {
        int index = 0;
        for (String type : types) {
            for (int i = 1; i <= 20; i++) {

                try {
                    ArrayList<String> linee = (ArrayList<String>) Files
                            .readAllLines(
                                    Paths.get("src/docs_200/" + type + "_"
                                            + (i < 10 ? "0" + i : i) + ".txt"),
                                    Charset.forName("CP850"));

                    ArrayList<ArrayList<String>> testiAnalizzati = tokenizerIT
                            .analizzaListaTesti(linee);

                    for (ArrayList<String> linea : testiAnalizzati) {
                        for (String parola : linea) {

                            if (!"".equals(parola)) {
                                if (dizionario.containsKey(parola)) {
                                    int[] documenti = dizionario.get(parola);
                                    documenti[index]++;
                                    dizionario.put(parola, documenti);
                                } else {
                                    int[] documenti = new int[200];
                                    documenti[index] = 1;
                                    dizionario.put(parola, documenti);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                index++;
            }
        }
    }

    public void generaDizionarioDaPathFile(String path) {
        try {
            ArrayList<String> linee = (ArrayList<String>) Files.readAllLines(
                    Paths.get(path), Charset.defaultCharset());
            ArrayList<ArrayList<String>> testiAnalizzati = tokenizerIT
                    .analizzaListaTesti(linee);
            for (ArrayList<String> lineaAnalizzata : testiAnalizzati) {
                for (String token : lineaAnalizzata) {
                    if (!"".equals(token)) {
                        if (dizionario.containsKey(token)) {
                            int[] occ = dizionario.get(token);
                            occ[0]++;
                            dizionario.put(token, occ);
                        } else {
                            int[] x = new int[1];
                            x[0] = 1;
                            dizionario.put(token, x);
                            System.out
                                    .println("Aggiunta una parola al dizionario quindi non saranno validi i calcoli");
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    // restituisce il dizionario in un array di stringhe
    public String[] getDizionario() {
        return dizionario.keySet().toArray(
                new String[dizionario.keySet().size()]);
    }

    public void addToDizionario(String linea, int[] valore) {
        dizionario.put(linea, valore);
    }

    // restituisce il numero di occorrenze della parola in ogni documento
    public int[] getOccorrenze(String parola) {
        return dizionario.get(parola);
    }

}
