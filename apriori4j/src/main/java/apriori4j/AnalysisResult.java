package apriori4j;

import java.util.Map;
import java.util.Set;

public class AnalysisResult {

    private final Map<Integer, Set<FrequentItemSet>> frequentItemSets;
    private final Set<AssociationRule> associationRules;

    public AnalysisResult(Map<Integer, Set<FrequentItemSet>> frequentItemSets, Set<AssociationRule> associationRules) {
        this.frequentItemSets = frequentItemSets;
        this.associationRules = associationRules;
    }

    public Map<Integer, Set<FrequentItemSet>> getFrequentItemSets() {
        return frequentItemSets;
    }

    public Set<AssociationRule> getAssociationRules() {
        return associationRules;
    }

    public String toString() {
        return "AnalysisResult(frequentItemSets: " + frequentItemSets + "associationRules: " + associationRules + ")";
    }

}
