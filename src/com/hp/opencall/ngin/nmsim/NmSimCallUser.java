package com.hp.opencall.ngin.nmsim;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.hp.opencall.ngin.scif.Address;
import com.hp.opencall.ngin.scif.Call;
import com.hp.opencall.ngin.scif.CallLeg;
import com.hp.opencall.ngin.scif.CallUser;
import com.hp.opencall.ngin.scif.Cause;
import com.hp.opencall.ngin.scif.Contact;
import com.hp.opencall.ngin.scif.PollEvent;
import com.hp.opencall.ngin.scif.ScifException;
import com.hp.opencall.ngin.scif.parameters.CommonParameterSet;
import com.hp.opencall.ngin.scif.parameters.CsCallParameterSet;

public class NmSimCallUser implements CallUser {

	private static Logger _log = Logger.getLogger(NmSimCallUser.class);
	private Call aCall = null;
	private DataSource datasource = null;

	public NmSimCallUser(Call aCall, DataSource datasource) {
		this.aCall = aCall;
		this.datasource = datasource;
	}

	@Override
	public void callAnswered(Cause cause, CallLeg leg) {
		_log.debug("Call Answered(cause/leg): " + cause + " "
				+ leg.getLegAddress());

	}

	@Override
	public void callEarlyAnswered(CallLeg leg) {
		_log.debug("Call EarlyAnswered(leg): " + leg);

	}

	@Override
	public void callEnd(Cause cause) {
		_log.debug("Call End(cause): " + cause);
		// aCall.abortCall(cause);

	}

	@Override
	public void callPoll(PollEvent poll) {
		_log.debug("Call Poll: " + poll);

	}

	@Override
	public void callStart(Cause cause, CallLeg leg, Contact contact) {
		_log.debug("Cause:" + cause);
		_log.debug("A Number - CallLeg: " + leg);
		_log.debug("B number - Contact: " + contact);
		_log.debug(aCall.getNetworkTechnology());
		_log.debug("ID - getId: " + aCall.getId());
		_log.debug("CDR: " + aCall.getCdr());

		switch (cause) {
		case NONE:
			try {

				CommonParameterSet commonpSet = aCall.getParameterSet(CommonParameterSet.class);
				_log.debug("Call Attempt time: " + commonpSet.getCallAttempTime());
				_log.debug("Call Service Key: " + commonpSet.getServiceKey());
				_log.debug("Call Network Call Type: " + commonpSet.getNetworkCallType());
				_log.debug("Call Switching Point: " + commonpSet.getSwitchingPoint());
				
				CsCallParameterSet cspSet = aCall.getParameterSet(CsCallParameterSet.class);
				_log.debug("CgPA IMSI: " + cspSet.getIMSI());
				_log.debug("MSC address:" + cspSet.getMscAddress().getUri());
				_log.debug("Location number" + cspSet.getLocationNumber().getUri());
				_log.debug("Localtion Information:" + cspSet.getLocationInformation());
				_log.debug("Trigger event type: " + cspSet.getEventTypeBCSM());
				
				if(commonpSet.getNetworkCallType().toString().equals("ORIGINATING")){
				 
				 Address newCgPA = NmSimMONewCgPA.getNewCgPA(datasource, leg);
				 _log.debug("newCgPA: " + newCgPA.getUri());
					if(!newCgPA.getUser().equals(leg.getLegAddress().getUser())){
						_log.debug("Set new calling party address(as camel generic number): " + newCgPA.getUri());
						commonpSet.setCallingParty(newCgPA);
					}
				
				aCall.continueCall();
				}
				else if (commonpSet.getNetworkCallType().toString().equals("TERMINATING")) {
					_log.warn("Call type is:" + commonpSet.getNetworkCallType().toString());
					aCall.forwardCall(contact);
					
				}
				else{
					_log.warn("Call type is not Originating, Forwarding or Terminating");
					_log.warn("Call type is:" + commonpSet.getNetworkCallType().toString());
				aCall.abortCall(Cause.INTERNAL);	
				}
				
			} catch (ScifException e) {
				_log.error("Cannot continue the call", e);
			}

			break;

		case DISCONNECTED:
			_log.error("Call Start, Cause: " + cause + "aborting the call");
			aCall.abortCall(Cause.DONE);
			break;

		case BUSY:
		case NOT_REACHABLE:
		case NO_ANSWER:
		case REJECTED:
		default:
			_log.error("Call Start, Cause: " + cause + "aborting the call");
			aCall.abortCall(Cause.DONE);
			break;
		}
	}

}
