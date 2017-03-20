package uk.vitalcode.dateparser.token

import java.time.{LocalDate, LocalTime}

import uk.vitalcode.dateparser.DateTokenAggregator

import scala.util.{Failure, Success, Try}

trait DateToken {
  val index: Int
}

object DateToken {

  def of(token: String, index: Int): Try[DateToken] = {
    val week = WeekDay.of(token, index)
    if (week.isSuccess) week
    else {
      val time = Time.of(token, index)
      if (time.isSuccess) time
      else {
        val year = Year.of(token, index)
        if (year.isSuccess) year
        else {
          val month = Month.of(token, index)
          if (month.isSuccess) month
          else {
            val day = Day.of(token, index)
            if (day.isSuccess) day
            else {
              val range = Range.of(token, index)
              if (range.isSuccess) range
              else Failure(new Exception(s"[$token] cannot be parsed as a date token"))
            }
          }
        }
      }
    }
  }

  def parse(text: String): List[DateToken] = {
    // Split on white space or "-" (range character, including "-" as return token, but not before AM/PM)
    val splitRegEx =
    """(?<![-])[\s]+(?![-]|PM|pm|AM|am)|(?=[-,])|(?<=[-,])""".r

    val dateTokens = splitRegEx.split(text).toList.filter(_.nonEmpty).flatMap(token => DateToken.of(token, 0) match {
      case Success(dateToken) =>
        List(dateToken)
      case _ =>
        Nil
    })
    DateTokenAggregator.indexTokenList(dateTokens)
  }
}

trait TokenCompanion[T] {
  def of(token: String, index: Int): Try[T]
}

final case class Date(value: LocalDate, index: Int) extends DateToken

object Date {

  def apply(year: Int, month: Int, day: Int, index: Int = 0): Try[Date] = Try {
    Date(LocalDate.of(year, month, day), index)
  }
}

final case class DateRange(from: LocalDate, to: LocalDate, index: Int) extends DateToken

object DateRange {
  def apply(range: ((Int, Int, Int), (Int, Int, Int)), index: Int = 0): DateRange = {
    val (from, to) = range
    DateRange(
      LocalDate.of(from._1, from._2, from._3),
      LocalDate.of(to._1, to._2, to._3),
      index
    )
  }
}

final case class TimeRange(from: LocalTime, to: LocalTime, index: Int) extends DateToken

object TimeRange {
  def apply(range: ((Int, Int), (Int, Int)), index: Int = 0): TimeRange = {
    val (from, to) = range
    TimeRange(
      LocalTime.of(from._1, from._2),
      LocalTime.of(to._1, to._2),
      index
    )
  }
}