package persistence
package database.slick

import com.github.tminglei.slickpg.*
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import scala.util.Try

trait MyPostgresProfile
    extends ExPostgresProfile
    with PgArraySupport
    with PgDate2Support
    with PgRangeSupport
    with PgHStoreSupport
    with PgPlayJsonSupport
    with PgSearchSupport
    with PgNetSupport
    with PgLTreeSupport {

  // jsonb support is in postgres 9.4.0 onward; for 9.3.x use "json"
  def pgjson =
    "jsonb"

  override protected def computeCapabilities: Set[slick.basic.Capability] =
    super.computeCapabilities + slick.jdbc.JdbcCapabilities.insertOrUpdate

  override val api = MyAPI

  object MyAPI
      extends ExtPostgresAPI
      with ArrayImplicits
      with Date2DateTimeImplicitsDuration
      with JsonImplicits
      with NetImplicits
      with LTreeImplicits
      with RangeImplicits
      with HStoreImplicits
      with SearchImplicits
      with SearchAssistants {
    implicit val strListTypeMapper: DriverJdbcType[List[String]] =
      new SimpleArrayJdbcType[String]("text").to(_.toList)
    implicit val playJsonArrayTypeMapper: DriverJdbcType[List[JsValue]] =
      new AdvancedArrayJdbcType[JsValue](
        pgjson,
        (s) =>
          utils.SimpleArrayUtils.fromString[JsValue](Json.parse(_))(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)
    implicit val playJsonArrayOptionTypeMapper
        : DriverJdbcType[List[Option[JsValue]]] =
      new AdvancedArrayJdbcType[Option[JsValue]](
        pgjson,
        (s) =>
          utils.SimpleArrayUtils
            .fromString[Option[JsValue]](value =>
              Try(Json.parse(value)).toOption
            )(s)
            .orNull,
        (v) =>
          utils.SimpleArrayUtils.mkString[Option[JsValue]](
            _.getOrElse(null).toString()
          )(v)
      ).to(_.toList)
  }
}

object MyPostgresProfile extends MyPostgresProfile
