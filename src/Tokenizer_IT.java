
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetConfiguration;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.data.BabelGloss;
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

/**
 *
 * La classe analizza morph-it_048_UTF8 e cerca di creare un tokenizzatore
 *
 */
public class Tokenizer_IT {

    static Set<String> stopWords;
    static HashMap<String, String> lemmi;
    static BabelNet bn;
    static List<BabelSynset> synsets;
    static boolean babel;

    // costruttore con scelta, booleana, per effettuare la tokenizzazione in
    // babelnet
    public Tokenizer_IT(boolean flag) {
        babel = flag;
        if (babel) {
            BabelNetConfiguration conf = BabelNetConfiguration.getInstance();
            conf.setConfigurationFile(new File(
                    "config/babelnet.properties"));
            bn = BabelNet.getInstance();
        }
        getStopWords();
        getLemmi();
    }

    // tokenizza una singola stringa di parole
    public ArrayList<String> analizzaTesto(String testo) {
        ArrayList<String> parole;
        ArrayList<String> parole_da_lem = tokenizeString(testo);
        if (parole_da_lem.size() > 0) {
            if (babel) {
                // versione con lemmi
                ArrayList<String> parole_lemmatizzate = lemmatizza(parole_da_lem);
                parole = getBabelNetID(parole_lemmatizzate);

                // versione senza lemmi
                // parole = getBabelNetID(parole_lemmatizzate);
            } else {
                parole = lemmatizza(parole_da_lem);
            }
        } else {
            parole = null;
        }

        return parole;

    }

    // tokenizza un ArrayList contenente stringhe di parole
    public ArrayList<ArrayList<String>> analizzaListaTesti(
            ArrayList<String> testi) {
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
                    parole.add(synset.getId().toString());
                } else {
                    parole.add(parola);
                    System.out.println("\t# " + parola);
                }
            } else {
                // TODO Auto-generated catch block

                List<BabelSynset> list_synset = bn.getSynsets(Language.IT,
                        parola);
                if (list_synset.size() > 0) {
                    synset = list_synset.get(0);
                    parole.add(synset.getId().toString());
                } else {
                    parole.add(parola);
                    System.out.println("\t# " + parola);
                }
            }
        }

        return parole;
    }

    private BabelSynset getSynsetFromSmallContext(String parola,
            List<String> context) {
        // TODO Auto-generated catch block

        List<BabelSynset> synsets = bn.getSynsets(Language.IT, parola);
        int o = 0, index = -1;
        for (BabelSynset synset : synsets) {
            List<BabelGloss> glosse = synset.getGlosses();
            int overlap = 0;
            for (BabelGloss glossa : glosse) {
                overlap += computeOverlap(context, glossa.getGloss());
            }
            if (o < overlap) {
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

    // lemmatizzazione di una stringa
    private ArrayList<String> lemmatizza(ArrayList<String> parole_da_lem) {
        ArrayList<String> parole = new ArrayList<>();
        for (String parola : parole_da_lem) {
            if (lemmi.containsKey(parola.toLowerCase())) {
                parole.add(lemmi.get(parola.toLowerCase()));
            } else {
                parole.add(parola.toLowerCase());
            }
        }
        return parole;
    }

    // Tokenizzazione di una parola rimuovendone le stopwords
    private ArrayList<String> tokenizeString(String stringa) {
        ArrayList<String> tokens = new ArrayList<>();
        String[] parole = stringa.split("[\\W]");
        for (String parola : parole) {
            if (!stopWords.contains(parola.toLowerCase()) && !parola.equals("")) {
                tokens.add(parola);
            }
        }
        return tokens;
    }

    // tokenizzazione di un ArrayList di stringhe rimuovendone le stopwords
//	private ArrayList<ArrayList<String>> tokenizeListStrings(
//			ArrayList<String> lista_stringhe) {
//		ArrayList<ArrayList<String>> lista_stringhe_tokenizzate = new ArrayList<ArrayList<String>>();
//		for (String stringa : lista_stringhe) {
//			ArrayList<String> stringa_tokenizzata = tokenizeString(stringa);
//			lista_stringhe_tokenizzate.add(stringa_tokenizzata);
//		}
//		return lista_stringhe_tokenizzate;
//	}
    private void getLemmi() {
        lemmi = new HashMap<>();

        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(
                    "src/morphit/morph-it_048.txt"));
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\\t");
                if (split.length == 3) {
                    lemmi.put(split[0], split[1]);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // popola l'hashset di stopwords leggendo le stopwords da file
    private void getStopWords() {

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

            br = new BufferedReader(new FileReader("src/stopwords_it.txt"));
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
    }

}
