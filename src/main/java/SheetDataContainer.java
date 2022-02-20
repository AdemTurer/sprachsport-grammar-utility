import java.util.Map;
import java.util.TreeMap;

public class SheetDataContainer {
    public Map<String, Object[]> adjectives_data = new TreeMap<>();
    public Map<String, Object[]> adverbs_data = new TreeMap<>();
    public Map<String, Object[]> conjunctions_data = new TreeMap<>();
    public Map<String, Object[]> interjections_data = new TreeMap<>();
    public Map<String, Object[]> nouns_data = new TreeMap<>();
    public Map<String, Object[]> prepositions_data = new TreeMap<>();
    public Map<String, Object[]> pronouns_data = new TreeMap<>();
    public Map<String, Object[]> verbs_data = new TreeMap<>();

    Map[] datasheets = {adjectives_data, adverbs_data, conjunctions_data, interjections_data, nouns_data, prepositions_data, pronouns_data ,verbs_data};

    public SheetDataContainer() {
        for(Map datasheet : datasheets){
            datasheet.put("1", new Object[]{"Word, Type, Count"});
        }
    }
}

