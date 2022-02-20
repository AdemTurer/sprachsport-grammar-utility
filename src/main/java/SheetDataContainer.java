import java.util.Map;
import java.util.TreeMap;

public class SheetDataContainer {
    public enum DATASHEETS {
        ADJECTIVES_DATA, ADVERBS_DATA, CONJUNCTIONS_DATA, INTERJECTIONS_DATA, NOUNS_DATA, PREPOSITIONS_DATA, PRONOUNS_DATA, VERBS_DATA
    }

    public Map<String, Object[]> adjectives_data = new TreeMap<>();
    public Map<String, Object[]> adverbs_data = new TreeMap<>();
    public Map<String, Object[]> conjunctions_data = new TreeMap<>();
    public Map<String, Object[]> interjections_data = new TreeMap<>();
    public Map<String, Object[]> nouns_data = new TreeMap<>();
    public Map<String, Object[]> prepositions_data = new TreeMap<>();
    public Map<String, Object[]> pronouns_data = new TreeMap<>();
    public Map<String, Object[]> verbs_data = new TreeMap<>();

    public Map[] datasheets = {adjectives_data, adverbs_data, conjunctions_data, interjections_data, nouns_data, prepositions_data, pronouns_data ,verbs_data};

    public SheetDataContainer() {
        for(Map datasheet : datasheets){
            datasheet.put("1", new Object[]{"Word", "Type", "Count"});
        }
    }

    public void push(Object[] rowData, SheetDataContainer.DATASHEETS datasheetName) {
        switch (datasheetName){
            case ADJECTIVES_DATA -> adjectives_data.put(Integer.toString(adjectives_data.size() + 1), rowData);
            case ADVERBS_DATA -> adverbs_data.put(Integer.toString(adverbs_data.size() + 1), rowData);
            case CONJUNCTIONS_DATA -> conjunctions_data.put(Integer.toString(conjunctions_data.size() + 1), rowData);
            case INTERJECTIONS_DATA -> interjections_data.put(Integer.toString(interjections_data.size() + 1), rowData);
            case NOUNS_DATA -> nouns_data.put(Integer.toString(nouns_data.size() + 1), rowData);
            case PREPOSITIONS_DATA -> prepositions_data.put(Integer.toString(prepositions_data.size() + 1), rowData);
            case PRONOUNS_DATA -> pronouns_data.put(Integer.toString(pronouns_data.size() + 1), rowData);
            case VERBS_DATA -> verbs_data.put(Integer.toString(verbs_data.size() + 1), rowData);
        }
    }
}

