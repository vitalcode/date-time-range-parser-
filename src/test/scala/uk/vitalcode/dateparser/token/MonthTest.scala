package uk.vitalcode.dateparser.token

class MonthTest extends TokenTest {

  "parsing string containing valid month value" in {

    "January" should beToken(Month(1))
    "March" should beToken(Month(3))
    "Sept." should beToken(Month(9))
    "Septemb" should beToken(Month(9))
    "Jun" should beToken(Month(6))
    "Nov" should beToken(Month(11))
    "May" should beToken(Month(5))
  }

  "parsing string containing invalid month value" in {

    "Sunday" shouldNot beToken[Month]
    "2017" shouldNot beToken[Month]
    "3:00PM" shouldNot beToken[Month]
  }
}