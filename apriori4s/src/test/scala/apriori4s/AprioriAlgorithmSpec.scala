package apriori4s

import java.io.File
import org.scalatest._
import scala.io.Source
import scala.util.control.NonFatal

class AprioriAlgorithmSpec extends FunSpec with Matchers {

  describe("AprioriAlgorithm") {
    it("should works with default parameters") {
      val apriori = new AprioriAlgorithm()
      val result = apriori.analyze(transactions)
      result.associationRules.size should equal(5)
    }
    it("should works with 0.8 or higher confidence") {
      val apriori = new AprioriAlgorithm(0.15, 0.8)
      val result = apriori.analyze(transactions)
      result.associationRules.size should equal(4)
    }
  }

  lazy val transactions: Seq[Transaction] = {
    val source = Source.fromFile(new File("apriori4j/src/test/resources/dataset.csv"))
    try {
      source.getLines.toList.map(line => new Transaction(line.split(",").toSet))
    } finally {
      try source.close()
      catch { case NonFatal(e) => }
    }
  }
}
