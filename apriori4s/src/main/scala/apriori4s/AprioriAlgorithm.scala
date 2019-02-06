package apriori4s

import apriori4j.{ AprioriAlgorithm => JavaAprioriAlgorithm }
import apriori4j.{ Transaction => JavaTransaction }
import scala.collection.JavaConverters._

case class AprioriAlgorithm(
  minSupport: Double = 0.15,
  minConfidence: Double = 0.6,
  maxItemSetSize: Int = 5,
  isQuickRun: Boolean = true,
  maxJoinedSetsSizeWhenQuickRun: Int = 2000,
  timeoutMillis: Int = 60000) {

  def analyze(transactions: Seq[Transaction]): AnalysisResult = {
    val javaApriori = {
      val a = new JavaAprioriAlgorithm(minSupport, minConfidence)
      a.setMaxItemSetSize(maxItemSetSize)
      a.setIsQuickRun(isQuickRun)
      a.setMaxJoinedSetsSizeWhenQuickRun(maxJoinedSetsSizeWhenQuickRun)
      a.setTimeoutMillis(timeoutMillis)
      a
    }
    val javaResult = javaApriori.analyze(transactions.map { t => new JavaTransaction(t.items.asJava) }.asJava)

    AnalysisResult(
      frequentItemSets = {
        javaResult.getFrequentItemSets.asScala.map {
          case (frequency, javaItemSets) =>
            val scalaItemSets: Set[FrequentItemSet] = javaItemSets.asScala
              .map(f => FrequentItemSet(f.getItemSet.asScala.toSet, f.getSupport))
              .toSet
            (frequency, scalaItemSets)
        }.toMap
      },
      associationRules = {
        javaResult.getAssociationRules.asScala.map { r =>
          new AssociationRule(
            leftHandSide = r.getLeftHandSide.asScala.toSet,
            rightHandSet = r.getRightHandSide.asScala.toSet,
            confidence = r.getConfidence)
        }.toSet
      })
  }

}