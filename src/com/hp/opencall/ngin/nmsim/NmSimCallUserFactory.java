package com.hp.opencall.ngin.nmsim;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.hp.opencall.ngin.scif.Call;
import com.hp.opencall.ngin.scif.CallUser;
import com.hp.opencall.ngin.scif.CallUserFactory;
import com.hp.opencall.ngin.scif.ScifException;

public class NmSimCallUserFactory implements CallUserFactory {

	private static Logger _log = Logger.getLogger(NmSimCallUserFactory.class);

	DataSource datasource = null;

	@Override
	public CallUser createUser(Object arg0, Call aCall) throws ScifException {
		_log.debug("calluserfactory returns new calluser instances" + arg0
				+ " " + aCall);
		return new NmSimCallUser(aCall, datasource);
	}

	@Override
	public String getName() {
		return "nmSIM";
	}

	public void setDataSource(DataSource datasource) {
		// TODO Auto-generated method stub
		this.datasource = datasource;
	}

}
