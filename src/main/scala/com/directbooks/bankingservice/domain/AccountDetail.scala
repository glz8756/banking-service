package com.directbooks.bankingservice.domain

import io.circe.derivation.{Configuration, ConfiguredCodec}

case class AccountDetail
(
  accountId: String,
  name: String,
  balance: Double
)derives ConfiguredCodec

object AccountDetail {
  given Configuration = Configuration.default.withSnakeCaseMemberNames
}
