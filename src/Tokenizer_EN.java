
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Tokenizer_EN {

    static StanfordCoreNLP pipeline;
    static Set<String> stopWords;

    public Tokenizer_EN() {
        stopWords = new HashSet<>();
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        pipeline = new StanfordCoreNLP(props);
        // ognuno deve settare il proprio path
        System.setProperty("wordnet.database.dir", "/usr/local/WordNet-3.0/dict/");
        getStopWords();
    }

    private static void getStopWords() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/stop_words_FULL.txt"));
            String word;
            while ((word = br.readLine()) != null) {
                stopWords.add(word);
            }
            br.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public ArrayList<ArrayList<String>> analizzaListaTesti(
            ArrayList<String> lines) {
        ArrayList<ArrayList<String>> lista_parole = new ArrayList<>();
        for (String testo : lines) {
            if (!"".equals(testo)) {
                ArrayList<String> testoAnalizzato = analizzaTesto(testo);
                if (testoAnalizzato != null) {
                    lista_parole.add(testoAnalizzato);
                }
            }
        }
        return lista_parole;
    }

    public ArrayList<String> analizzaTesto(String testo) {
        ArrayList<String> parole;
        String parole_da_lem = tokenizeString(testo);
        if (!"".equals(parole_da_lem)) {
            parole = lemmatizza(parole_da_lem);
        } else {
            parole = null;
        }
        return parole;

    }

    private ArrayList<String> lemmatizza(String parole_da_lem) {
        ArrayList<String> lem = new ArrayList<>();
        Annotation doc = new Annotation(parole_da_lem);
        pipeline.annotate(doc);
        List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
        for (CoreMap s : sentences) {
            for (CoreLabel token : s.get(TokensAnnotation.class)) {
                // this is the text of the token
                lem.add(token.get(LemmaAnnotation.class));
            }
        }

        return lem;
    }

    private String tokenizeString(String testo) {
        String[] splitted = testo.toLowerCase().split("[\\W]");
        testo = "";
        for (String s : splitted) {
            if (stopWords.contains(s) != true) {
                testo += s + " ";
            }
        }
        return testo;
    }

}
