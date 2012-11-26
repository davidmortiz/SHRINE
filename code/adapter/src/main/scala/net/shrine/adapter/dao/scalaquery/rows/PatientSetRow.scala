package net.shrine.adapter.dao.scalaquery.rows

/**
 * @author clint
 * @date Oct 16, 2012
 */
final case class PatientSetRow(
  id: Int,
  resultId: Int,
  patientId: String) extends ResultRow(id, resultId)