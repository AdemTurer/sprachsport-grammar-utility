import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import javax.swing.*;
import java.io.File;
import java.util.logging.Logger;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main  {
    private static final Logger log = Logger.getLogger("Main.class");

    public static void main(String[] args) throws Exception {
        //File selection dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(new JFrame());
        if (result != JFileChooser.APPROVE_OPTION) return;
        File selectedFile = fileChooser.getSelectedFile();
        if (!selectedFile.getName().endsWith(".txt")) {
            log.info("Your document must be a .txt file.");
            return;
        }

        //READING AND PARSING FILE
        MaxentTagger tagger = new MaxentTagger("res/english-bidirectional-distsim.tagger");
        BufferedReader br = new BufferedReader(new FileReader(selectedFile));
        String line;
        StringBuilder fullTextBuilder = new StringBuilder();
        while ((line = br.readLine())!= null){
            log.info("Read line from " + selectedFile.getName() + ": " + line);
            fullTextBuilder.append(line).append(" ");
        }

        String fullText = fullTextBuilder.toString();
        String taggedFullText = tagger.tagString(fullText);
        List<TaggedWord> parsedFullText = new ArrayList<>();
        String[] spaced = taggedFullText.split(" "); //TODO: Assumes spacing is perfect.

        for(String s : spaced){
            String[] parsedWord = s.split("_"); //TODO: Does not account for the edge case that the character "_" is contained in the text.
            parsedFullText.add(new TaggedWord(parsedWord[0].toLowerCase(Locale.ENGLISH), parsedWord[1]));
        }

        Map<TaggedWord, Integer> parsedFullTextWithWordCount = new HashMap<>();

        for (TaggedWord twd : parsedFullText) {
            Integer count = parsedFullTextWithWordCount.get(twd);
            parsedFullTextWithWordCount.put(twd, (count == null) ? 1 : count + 1);
        }

        //debug code
        printMap(parsedFullTextWithWordCount);
        //READING AND PARSING FILE - END

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet adjectives_sheet = workbook.createSheet("Adjectives");
        XSSFSheet adverbs_sheet = workbook.createSheet("Adverbs");
        XSSFSheet conjunctions_sheet = workbook.createSheet("Conjunctions");
        XSSFSheet interjections_sheet = workbook.createSheet("Interjections");
        XSSFSheet nouns_sheet = workbook.createSheet("Nouns");
        XSSFSheet prepositions_sheet = workbook.createSheet("Prepositions");
        XSSFSheet pronouns_sheet = workbook.createSheet("Pronouns");
        XSSFSheet verbs_sheet = workbook.createSheet("Verbs");
    }

    public static void printMap(Map<TaggedWord, Integer> map){
        for (Map.Entry<TaggedWord, Integer> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : "
                    + entry.getValue());
        }
    }
}

