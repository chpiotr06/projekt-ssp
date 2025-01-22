package pl.edu.agh.kt;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;

public class AddressConfig {
	private MacAddress srcMac;
	private MacAddress dstMac;
	private IPv4Address srcIp;
	private IPv4Address dstIp;
	private OFPort outPort;

	public AddressConfig(String dstMac, String srcMac, String srcIp,
			String dstIp, OFPort outPort) {
		this.dstMac = MacAddress.of(dstMac);
		this.srcMac = MacAddress.of(srcMac);
		this.srcIp = IPv4Address.of(srcIp);
		this.dstIp = IPv4Address.of(dstIp);
		this.outPort = outPort;
	}

	public MacAddress getDstMac() {
		return dstMac;
	}

	public MacAddress getSrcMac() {
		return srcMac;
	}

	public IPv4Address getSrcIp() {
		return srcIp;
	}

	public IPv4Address getDstIp() {
		return dstIp;
	}

	public OFPort getOutPort() {
		return outPort;
	}

}
