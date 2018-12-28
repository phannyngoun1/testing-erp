package net.dream.erp.usercase.port

case class EventBody(persistenceId: String, sequenceNr: Long, event: Any)
