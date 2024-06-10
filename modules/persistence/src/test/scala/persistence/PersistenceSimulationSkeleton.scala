package persistence

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.body.Body
import io.gatling.core.structure.ChainBuilder

abstract class PersistenceSimulationSkeleton extends Simulation {
  val operations: List[ChainBuilder] = operationList

  val httpProtocol = http
    .baseUrl("http://localhost:9021")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36")

  val headers_0 = Map(
  		"Cache-Control" -> "max-age=0",
  		"Sec-Fetch-Dest" -> "document",
  		"Sec-Fetch-Mode" -> "navigate",
  		"Sec-Fetch-Site" -> "none",
  		"Sec-Fetch-User" -> "?1",
  		"sec-ch-ua" -> """Google Chrome";v="125", "Chromium";v="125", "Not.A/Brand";v="24""",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-ch-ua-platform" -> "macOS"
  )

  def buildScenario(name: String) =
    scenario(name)
      .exec(
        operations.reduce((a, b) => a.pause(1.second).exec(b))
      )

  def executeOperations(): Unit
}
