
import it.uniroma1.lcl.babelnet.BabelGloss;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetConfiguration;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.jlt.util.Language;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * La classe analizza morph-it_048_UTF8 e cerca di creare un tokenizzatore
 *
 */
public class Tokenizer_IT {

    Set<String> stopWords;
    HashMap<String, String> lemmi;
    BabelNet bn;
    boolean babel;
    boolean print;

    // costruttore con scelta, booleana, per effettuare la tokenizzazione in
    // babelnet
    public Tokenizer_IT(boolean flag, boolean print) {
        babel = flag;
        if (babel) {
            BabelNetConfiguration conf = BabelNetConfiguration.getInstance();
            conf.setConfigurationFile(new File(
                    "config/babelnet.properties"));
            bn = BabelNet.getInstance();
        } else {
            lemmi = getLemmi();

        }
        stopWords = getStopWords();
        this.print = print;
    }

    // popola l'hashset di stopwords leggendo le stopwords da file
    private Set<String> getStopWords() {

        stopWords = new LinkedHashSet<>();

        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(
                    "src/stopwords.txt"));
            while ((line = br.readLine()) != null) {
                if (!line.contains("|") && !line.equals("")) {
                    stopWords.add(line);
                }
            }
            br.close();

            br = new BufferedReader(new FileReader("resources/jlt/stopwords/stopwords_it.txt"));
            while ((line = br.readLine()) != null) {
                stopWords.add(line);
            }
            br.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return stopWords;
    }

    private HashMap<String, String> getLemmi() {
        lemmi = new HashMap<>();

        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(
                    "src/morphit/morph-it_048.txt"));
            while ((line = br.readLine()) != null) {
                if (line.indexOf("|") < 0 && !line.equals("")) {
                    String[] row = line.split("[\\s]+");
                    lemmi.put(row[0], row[1]);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lemmi;
    }

    // tokenizza un ArrayList contenente stringhe di parole
    public ArrayList<ArrayList<String>> analizzaListaTesti(ArrayList<String> testi) {
        ArrayList<ArrayList<String>> lista_parole = new ArrayList<>();
        for (String testo : testi) {
            if (!"".equals(testo)) {
                ArrayList<String> testoAnalizzato = analizzaTesto(testo);
                if (testoAnalizzato != null) {
                    lista_parole.add(testoAnalizzato);
                }
            }
        }
        return lista_parole;
    }

    // tokenizza una singola stringa di parole
    public ArrayList<String> analizzaTesto(String testo) {
        ArrayList<String> parole;
        ArrayList<String> parole_da_lem = tokenizeString(testo);
        if (parole_da_lem.size() > 0) {
            if (babel) {
                parole = getBabelNetID(parole_da_lem);
                if (print) {
                    System.out.println(parole);
                }
            } else {
                parole = lemmatizza(parole_da_lem);
            }
        } else {
            parole = null;
        }
        return parole;

    }

    // lemmatizzazione di una stringa
    private ArrayList<String> lemmatizza(ArrayList<String> parole_da_lem) {
        ArrayList<String> parole = new ArrayList<>();
        for (String parola_da_lem : parole_da_lem) {
            String lemma = lemmi.get(parola_da_lem.toLowerCase());
            if (lemma != null) {
                parole.add(lemma);
            } else {
                parole.add(parola_da_lem);
            }
        }
        return parole;
    }

    // Tokenizzazione di una parola rimuovendone le stopwords
    private ArrayList<String> tokenizeString(String stringa) {
        ArrayList<String> tokens = new ArrayList<>();
        String[] parole = stringa.toLowerCase().split("[\\W]");
        for (String parola : parole) {
            if (!stopWords.contains(parola.toLowerCase()) && !parola.equals("")) {
                tokens.add(parola);
            }
        }
        return tokens;
    }

    private ArrayList<String> getBabelNetID(ArrayList<String> parole_da_lem) {
        ArrayList<String> parole = new ArrayList<>();
        for (int i = 0; i < parole_da_lem.size(); i++) {
            List<String> context;
            String parola;
            if (parole_da_lem.size() > 5) {
                parola = parole_da_lem.get(i);

                if (i == 1) {
                    context = parole_da_lem.subList(1, i + 3);
                } else if (i == 0) {
                    context = parole_da_lem.subList(0, i + 4);
                } else if (i == parole_da_lem.size() - 1) {
                    context = parole_da_lem.subList(i - 3, i);
                } else if (i == parole_da_lem.size() - 2) {
                    context = parole_da_lem.subList(i - 4, i);
                } else {
                    context = parole_da_lem.subList(i - 2, i + 2);
                }

            } else {
                parola = parole_da_lem.get(i);
                context = parole_da_lem;
            }

            BabelSynset synset;
            if (context.size() > 0) {
                synset = getSynsetFromSmallContext(parola, context);
                if (synset != null) {
                    parole.add(synset.getId());
                } else {
                    parole.add(parola);
                }
            } else {
                List<BabelSynset> list_synset = new ArrayList<>();
                try {
                    list_synset = bn.getSynsets(Language.IT,
                            parola);
                } catch (IOException ex) {
                    Logger.getLogger(Tokenizer_IT.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (list_synset.size() > 0) {
                    synset = list_synset.get(0);
                    parole.add(synset.getId());
                } else {
                    parole.add(parola);
                }
            }
        }

        return parole;
    }

    private BabelSynset getSynsetFromSmallContext(String parola, List<String> context) {
        List<BabelSynset> synsets = new ArrayList<>();
        try {
            synsets = bn.getSynsets(Language.IT, parola);
        } catch (IOException ex) {
            Logger.getLogger(Tokenizer_IT.class.getName()).log(Level.SEVERE, null, ex);
        }
        int o = 0, index = -1;
        for (BabelSynset synset : synsets) {
            List<BabelGloss> glosse = new ArrayList<>();
            try {
                glosse = synset.getGlosses();
            } catch (IOException ex) {
                Logger.getLogger(Tokenizer_IT.class.getName()).log(Level.SEVERE, null, ex);
            }
            int overlap = 0;
            for (BabelGloss glossa : glosse) {
                overlap += computeOverlap(context, glossa.getGloss());
            }
            if (o < overlap / glosse.size()) {
                index = synsets.indexOf(synset);
            }
        }
        if (index == -1) {
            return null;
        } else {
            return synsets.get(index);
        }

    }

    // calcola il numero di occorrenze di una parola in una glossa
    private int computeOverlap(List<String> context, String glossa) {
        ArrayList<String> lista_parole = tokenizeString(glossa);
        int overlap = 0;
        for (String parola : lista_parole) {
            if (context.contains(parola)) {
                overlap++;
            }
        }
        return overlap;
    }

    public ArrayList<String> removeStopwordsFromContexts(String frase) {
        ArrayList<String> array_tokens = new ArrayList<>();
        String[] words = frase.split("[\\s]+");
        for (String word : words) {
            String clean_word = word.replaceAll("[ \\p{Punct}]", " ");
            String[] tokens = clean_word.split("[\\s]+");
            for (String token : tokens) {
                if (!stopWords.contains(token.toLowerCase())) {
                    array_tokens.add(token);
                }
            }
        }
        return array_tokens;
    }

    public ArrayList<ArrayList<String>> removeStopwordsFromList(ArrayList<String> lista) {
        ArrayList<ArrayList<String>> lista_stopwords_rimosse = new ArrayList<>();
        for (String word : lista) {
            lista_stopwords_rimosse.add(removeStopwordsFromContexts(word));
        }
        return lista_stopwords_rimosse;
    }

    public ArrayList<ArrayList<String>> getLemsFromContexts(ArrayList<ArrayList<String>> lems) {
        ArrayList<ArrayList<String>> lista_lemmi_contesti = new ArrayList<>();

        for (int i = 0; i < lems.size(); i++) {
            ArrayList<String> lemmi_contesto = new ArrayList<>();
            for (int j = 0; j < lems.get(i).size(); j++) {
                String lemma = lemmi.get(lems.get(i).get(j));
                if (lemma != null) {
                    lemmi_contesto.add(lemma);
                } else {
                    lemmi_contesto.add(lems.get(i).get(j));
                }
            }
            lista_lemmi_contesti.add(lemmi_contesto);
        }
        return lista_lemmi_contesti;
    }
}
