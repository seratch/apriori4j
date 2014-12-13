import apriori4j.AnalysisResult;
import apriori4j.AprioriAlgorithm;
import apriori4j.ItemSet;
import apriori4j.Transaction;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class AprioriAlgorithmTest {

    @SuppressWarnings("unchecked")
    public static List<Transaction> getTransactions() throws IOException {
        List<String> lines = FileUtils.readLines(new File("apriori4j/src/test/resources/dataset.csv"));
        List<Transaction> transactions = new ArrayList<Transaction>();
        for (String line : lines) {
            Set<String> items = new HashSet<String>();
            for (String item : line.split(",")) {
                items.add(item);
            }
            transactions.add(new Transaction(items));
        }
        return transactions;
    }

    @Test
    public void itemSetEquality() {
        Map<ItemSet, Integer> v = new HashMap<ItemSet, Integer>();
        v.put(ItemSet.create("1"), 123);
        assertThat(v.get(ItemSet.create("1")), is(equalTo(123)));
    }

    @Test
    public void verifyWithDatasetCsv1() throws Exception {
        AprioriAlgorithm apriori = new AprioriAlgorithm(0.15, 0.6);
        AnalysisResult result = apriori.analyze(getTransactions());
        assertThat(result.getAssociationRules().size(), is(equalTo(5)));
    }

    @Test
    public void verifyWithDatasetCsv2() throws Exception {
        AprioriAlgorithm apriori = new AprioriAlgorithm(0.15, 0.8);
        AnalysisResult result = apriori.analyze(getTransactions());
        assertThat(result.getAssociationRules().size(), is(equalTo(4)));
    }

}
