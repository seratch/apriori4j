import apriori4j.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class AprioriAlgorithmTest {

    private static final Logger logger = LoggerFactory.getLogger(AprioriAlgorithmTest.class);

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

    public void runWithFixedItemSizePattern(int size) throws AprioriTimeoutException {
        SecureRandom r = new SecureRandom();
        List<Transaction> transactions = new ArrayList<Transaction>();
        for (int t = 0; t < 10000; t++) {
            Set<String> items = new HashSet<String>();
            for (int i = 0; i < size; i++) {
                items.add("A" + r.nextInt(5));
            }
            transactions.add(new Transaction(items));
        }
        logger.info("runWithFixedItemSizePattern - transactions: " + transactions.size());
        long before = System.currentTimeMillis();
        AnalysisResult result = new AprioriAlgorithm(0.15, 0.8).analyze(transactions);
        long millis = System.currentTimeMillis() - before;
        logger.info("runWithFixedItemSizePattern - itemSetSize: " + size + ", found association rules: " + result.getAssociationRules().size() + ", time: " + millis + " ms");
    }

    @Test
    public void FixedItemSizePatterns() throws AprioriTimeoutException {
        runWithFixedItemSizePattern(1);
        runWithFixedItemSizePattern(2);
        runWithFixedItemSizePattern(3);
        runWithFixedItemSizePattern(4);
        runWithFixedItemSizePattern(5);
        runWithFixedItemSizePattern(10);
    }

    public void runWithVariousItemSizePattern(int size) throws AprioriTimeoutException {
        SecureRandom r = new SecureRandom();
        List<Transaction> transactions = new ArrayList<Transaction>();
        for (int t = 0; t < size; t++) {
            Set<String> items = new HashSet<String>();
            for (int i = 0; i < t; i++) {
                items.add("A" + (i + r.nextInt(10)));
            }
            transactions.add(new Transaction(items));
        }
        logger.info("runWithVariousItemSizePattern - transactions: " + transactions.size());
        long before = System.currentTimeMillis();
        AnalysisResult result = new AprioriAlgorithm(0.15, 0.8).analyze(transactions);
        long millis = System.currentTimeMillis() - before;
        logger.info("runWithVariousItemSizePattern - itemSetSize: " + size + ", found association rules: " + result.getAssociationRules().size() + ", time: " + millis + " ms");
    }

    @Test
    public void variousItemSizePatterns() throws AprioriTimeoutException {
        runWithVariousItemSizePattern(10);
        runWithVariousItemSizePattern(20);
        runWithVariousItemSizePattern(30);
        runWithVariousItemSizePattern(50);
        runWithVariousItemSizePattern(100);
        runWithVariousItemSizePattern(1000);
    }

    public void runWithVariousItemSizePattern2(int size) throws AprioriTimeoutException {
        List<Transaction> transactions = new ArrayList<Transaction>();
        for (int t = 0; t < size; t++) {
            Set<String> items = new HashSet<String>();
            for (int i = 0; i < t; i++) {
                items.add("A" + (i + t));
            }
            transactions.add(new Transaction(items));
        }
        logger.info("runWithVariousItemSizePattern2 - transactions: " + transactions.size());
        long before = System.currentTimeMillis();
        AnalysisResult result = new AprioriAlgorithm(0.15, 0.8).analyze(transactions);
        long millis = System.currentTimeMillis() - before;
        logger.info("runWithVariousItemSizePattern2 - itemSetSize: " + size + ", found association rules: " + result.getAssociationRules().size() + ", time: " + millis + " ms");
    }

    @Test
    public void variousItemSizePatterns2() throws AprioriTimeoutException {
        runWithVariousItemSizePattern2(10);
        runWithVariousItemSizePattern2(20);
        runWithVariousItemSizePattern2(30);
        runWithVariousItemSizePattern2(50);
        runWithVariousItemSizePattern2(100);
        runWithVariousItemSizePattern2(1000);
    }

    @Test(expected = AprioriTimeoutException.class)
    public void timeout() throws Exception {
        AprioriAlgorithm apriori = new AprioriAlgorithm(0.15, 0.6);
        apriori.setTimeoutMillis(10);
        apriori.analyze(getTransactions());
    }

}
