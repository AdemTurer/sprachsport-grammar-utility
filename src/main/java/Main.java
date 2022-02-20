import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.*;
import javax.swing.*;
import java.io.File;
import java.util.logging.Logger;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main  {
    private static final Logger log = Logger.getLogger("Main.class");

    public static void main(String[] args) throws Exception {
        //File selection dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        JFrame fileSelectionDialog = new JFrame();
        int result = fileChooser.showOpenDialog(fileSelectionDialog);
        if (result != JFileChooser.APPROVE_OPTION){
            fileSelectionDialog.dispose();
            return;
        }
        File selectedFile = fileChooser.getSelectedFile();
        if (!selectedFile.getName().endsWith(".txt")) {
            log.info("Your document must be a .txt file.");
            fileSelectionDialog.dispose();
            return;
        }
        fileSelectionDialog.dispose();

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

        //READING AND PARSING FILE - END
        //WRITING TO EXCEL FILE - START
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet adjectives_sheet = workbook.createSheet("Adjectives");
        XSSFSheet adverbs_sheet = workbook.createSheet("Adverbs");
        XSSFSheet conjunctions_sheet = workbook.createSheet("Conjunctions");
        XSSFSheet interjections_sheet = workbook.createSheet("Interjections");
        XSSFSheet nouns_sheet = workbook.createSheet("Nouns");
        XSSFSheet prepositions_sheet = workbook.createSheet("Prepositions");
        XSSFSheet pronouns_sheet = workbook.createSheet("Pronouns");
        XSSFSheet verbs_sheet = workbook.createSheet("Verbs");
        SheetDataContainer sheetData = new SheetDataContainer();

        for(TaggedWord twd : parsedFullTextWithWordCount.keySet()) {
            switch (twd.tag()) {
                case "JJ" -> sheetData.push(new Object[]{twd.word(), "Adjective", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.ADJECTIVES_DATA);
                case "JJR" -> sheetData.push(new Object[]{twd.word(), "Adjective, comparative", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.ADJECTIVES_DATA);
                case "JJS" -> sheetData.push(new Object[]{twd.word(), "Adjective, superlative", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.ADJECTIVES_DATA);

                case "RB" -> sheetData.push(new Object[]{twd.word(), "Adverb", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.ADVERBS_DATA);
                case "RBR" -> sheetData.push(new Object[]{twd.word(), "Adverb, comparative", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.ADVERBS_DATA);
                case "RBS" -> sheetData.push(new Object[]{twd.word(), "Adverb, superlative", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.ADVERBS_DATA);
                case "WRB" -> sheetData.push(new Object[]{twd.word(), "Adverb, Wh-", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.ADVERBS_DATA);

                case "CC" -> sheetData.push(new Object[]{twd.word(), "Conjunction, coordinating", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.CONJUNCTIONS_DATA);
                case "IN" -> {
                    sheetData.push(new Object[]{twd.word(), "Conjunction, subordinating OR preposition", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.CONJUNCTIONS_DATA);
                    sheetData.push(new Object[]{twd.word(), "Preposition OR subordinating conjunction", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.PREPOSITIONS_DATA);
                }

                case "UH" -> sheetData.push(new Object[]{twd.word(), "Interjection", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.INTERJECTIONS_DATA);

                case "NN" -> sheetData.push(new Object[]{twd.word(), "Noun, singular or mass", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.NOUNS_DATA);
                case "NNS" -> sheetData.push(new Object[]{twd.word(), "Noun, plural", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.NOUNS_DATA);
                case "NNP" -> sheetData.push(new Object[]{twd.word(), "Noun, proper and singular", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.NOUNS_DATA);
                case "NNPS" -> sheetData.push(new Object[]{twd.word(), "Noun, proper and plural", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.NOUNS_DATA);

                case "PRP" -> sheetData.push(new Object[]{twd.word(), "Pronoun, personal", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.PRONOUNS_DATA);
                case "PRP$" -> sheetData.push(new Object[]{twd.word(), "Pronoun, possessive", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.PRONOUNS_DATA);
                case "WP" -> sheetData.push(new Object[]{twd.word(), "Pronoun, Wh-", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.PRONOUNS_DATA);
                case "WP$" -> sheetData.push(new Object[]{twd.word(), "Pronoun, possessive Wh-", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.PRONOUNS_DATA);

                case "VB" -> sheetData.push(new Object[]{twd.word(), "Verb, base form", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.VERBS_DATA);
                case "VBD" -> sheetData.push(new Object[]{twd.word(), "Verb, past tense", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.VERBS_DATA);
                case "VBG" -> sheetData.push(new Object[]{twd.word(), "Verb, gerund or present participle", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.VERBS_DATA);
                case "VBN" -> sheetData.push(new Object[]{twd.word(), "Verb, past participle", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.VERBS_DATA);
                case "VBP" -> sheetData.push(new Object[]{twd.word(), "Verb, non-3rd person singular present", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.VERBS_DATA);
                case "VBZ" -> sheetData.push(new Object[]{twd.word(), "Verb, 3rd person singular present", Integer.toString(parsedFullTextWithWordCount.get(twd))}, SheetDataContainer.DATASHEETS.VERBS_DATA);
            }
        }

        writeToSpreadsheet(adjectives_sheet, sheetData.adjectives_data);
        writeToSpreadsheet(adverbs_sheet, sheetData.adverbs_data);
        writeToSpreadsheet(conjunctions_sheet, sheetData.conjunctions_data);
        writeToSpreadsheet(interjections_sheet, sheetData.interjections_data);
        writeToSpreadsheet(nouns_sheet, sheetData.nouns_data);
        writeToSpreadsheet(prepositions_sheet, sheetData.prepositions_data);
        writeToSpreadsheet(pronouns_sheet, sheetData.pronouns_data);
        writeToSpreadsheet(verbs_sheet, sheetData.verbs_data);

        FileOutputStream out = new FileOutputStream(selectedFile.getName().substring(0, selectedFile.getName().length()-4) + "_pos_info.xlsx");
        workbook.write(out);
        out.close();
        //WRITING TO EXCEL FILE - END
    }

    public static void writeToSpreadsheet(XSSFSheet spreadsheet, Map<String, Object[]> sheetData){
        XSSFRow row;
        int rowId = 0;
        for (String key : sheetData.keySet()) {
            row = spreadsheet.createRow(rowId++);
            Object[] objectArr = sheetData.get(key);
            int cellId = 0;

            for (Object obj : objectArr) {
                Cell cell = row.createCell(cellId++);
                cell.setCellValue((String)obj);
            }
        }
    }
}

