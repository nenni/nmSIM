package com.hp.opencall.ngin.nmsim;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.hp.opencall.ngin.scif.CallProvider;
import com.hp.opencall.ngin.scif.ScifException;
import com.hp.opencall.ngin.scif.ScifFactory;
import com.hp.opencall.ngin.scif.imsc.ScifImslet;

public class NmSimImslet extends ScifImslet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1;
	private static Logger _log = Logger.getLogger(NmSimImslet.class);

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		_log.debug("nmSIM Destroying...");
	}

	@Override
	public void gracefulShutdown() throws javax.servlet.ServletException {
		_log.debug("nmSIM graceful shutdown....");
		super.gracefulShutdown();
	}

	@Override
	public void init() throws javax.servlet.ServletException {
		_log.debug("nmSIM init...");
		super.init();
	}

	@Override
	public void unlocked() throws javax.servlet.ServletException {

		super.unlocked();

		String callProviderTechno = super.getImsletContext().getInitParameter(
				"NgIn.Srv.nmSIM.CALL_PROVIDER_TECHNO");
		String callProviderName = super.getImsletContext().getInitParameter(
				"NgIn.Srv.nmSIM.CALL_PROVIDER_NAME");
		String dataSourceName = super.getImsletContext().getInitParameter(
				"NgIn.Srv.nmSIM.DATA_SOURCE_NAME");

		NmSimCallUserFactory factory = new NmSimCallUserFactory();
		_log.debug("UNLOCKED DEBuG MESSAGE" + callProviderName + " "
				+ callProviderTechno);

		if (callProviderTechno.equals("CS")
				|| callProviderTechno.equals("SCXML")) {
			_log.info("Call Provider Techno: " + callProviderTechno + " # "
					+ "Call Provider Name: " + callProviderName);
			try {

				DataSource datasource = getJdbcDataSource(dataSourceName);
				factory.setDataSource(datasource);
				CallProvider callProvider = ScifFactory.instance()
						.getCallProvider(callProviderTechno, callProviderName);
				callProvider.addCallUserFactory(factory, null);

			} catch (ScifException e) {
				_log.error("Provider cannot be found", e);
			}

		} else {
			_log.error("Wrong techology. Technology must be CS or SCXML.");
		}

	}

}
