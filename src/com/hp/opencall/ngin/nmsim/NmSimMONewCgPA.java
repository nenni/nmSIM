package com.hp.opencall.ngin.nmsim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import com.hp.opencall.ngin.scif.Address;
import com.hp.opencall.ngin.scif.CallLeg;

public class NmSimMONewCgPA {

	private static Connection databaseConnection = null;
	private static PreparedStatement pStatement = null;
	private static ResultSet rSet;

	private static Logger _log = Logger.getLogger(NmSimMONewCgPA.class);

	public static Address getNewCgPA(DataSource datasource, CallLeg leg) {

		String amNumber = null;
		Address newCgPA = null;

		try {
			databaseConnection = datasource.getConnection();
			String aNumber = leg.getLegAddress().getUser();

			_log.debug("aNumber:" + aNumber);

			pStatement = databaseConnection
					.prepareStatement(""
							+ "select g.master_member from nmsim_group g, nmsim_members m where m.member = ? and g.group_id=m.group_id");
			pStatement.setString(1, aNumber);
			rSet = pStatement.executeQuery();

			if (rSet.next()) {
				amNumber = rSet.getString(1);
				newCgPA = new Address(
						"tel:"
								+ amNumber
								+ ";network-context=Q763.4.1;presentation=0;screening=3");
				_log.debug("amNumber: " + amNumber);
				_log.debug("newCgPA: " + newCgPA.getUri());

			}
			return newCgPA;

		} catch (SQLException e) {
			_log.error("DB error: ", e);
			return null;
		} finally {
			if (rSet != null)
				try {
					rSet.close();
				} catch (SQLException e) {
				}
			if (pStatement != null)
				try {
					pStatement.close();
				} catch (SQLException e) {
				}
			if (databaseConnection != null)
				try {
					databaseConnection.close();
				} catch (SQLException e) {
				}
		}

	}
}
