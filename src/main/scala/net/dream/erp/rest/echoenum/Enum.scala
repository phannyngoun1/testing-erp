package net.dream.erp.rest.echoenum

case object Enum extends Enumeration(0) {
  type Enum = Value
  val TALL = Value("TALL")
  val GRANDE = Value("GRANDE")
  val VENTI = Value("VENTI")
}
