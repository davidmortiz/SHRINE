package net.shrine.adapter

import net.shrine.adapter.dao.I2b2AdminPreviousQueriesDao
import net.shrine.adapter.dao.slick.SlickI2b2AdminPreviousQueriesDao

/**
 * @author clint
 * @date Apr 24, 2013
 */
trait HasI2b2AdminPreviousQueriesDao { self: AdapterDbTest =>
  protected def i2b2AdminDao: I2b2AdminPreviousQueriesDao = new SlickI2b2AdminPreviousQueriesDao(database, tables)
}