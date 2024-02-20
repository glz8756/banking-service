package com.directbooks.bankingservice.utils

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}


object Utils {

  def getTransactionDate(): String =
    DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
      .format(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()))

  def getUUID() = java.util.UUID.randomUUID

}
