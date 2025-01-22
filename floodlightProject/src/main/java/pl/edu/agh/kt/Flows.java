package pl.edu.agh.kt;

import java.util.ArrayList;
import java.util.List;

import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.OFVlanVidMatch;
import org.projectfloodlight.openflow.types.VlanVid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;

public class Flows {

	private static final Logger logger = LoggerFactory.getLogger(Flows.class);

	public static short FLOWMOD_DEFAULT_IDLE_TIMEOUT = 5;
	public static short FLOWMOD_DEFAULT_HARD_TIMEOUT = 7;
	public static short FLOWMOD_DEFAULT_PRIORITY = 100;

	protected static boolean FLOWMOD_DEFAULT_MATCH_VLAN = false;
	protected static boolean FLOWMOD_DEFAULT_MATCH_MAC = true;
	protected static boolean FLOWMOD_DEFAULT_MATCH_IP_ADDR = true;
	protected static boolean FLOWMOD_DEFAULT_MATCH_TRANSPORT = true;

	public Flows() {
		logger.info("Flows constructor");
	}

	public static void simpleAdd(IOFSwitch sw, OFPacketIn pin,
			FloodlightContext cntx, OFPort outPort) {
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd();
		Match m = createMatchFromPacket(sw, pin.getInPort(), cntx);

		OFActionOutput.Builder aob = sw.getOFFactory().actions().buildOutput();
		List<OFAction> actions = new ArrayList<>();
		aob.setPort(outPort);
		aob.setMaxLen(Integer.MAX_VALUE);
		actions.add(aob.build());

		fmb.setMatch(m).setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
				.setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
				.setBufferId(pin.getBufferId()).setOutPort(outPort)
				.setPriority(FLOWMOD_DEFAULT_PRIORITY);

		fmb.setActions(actions);

		try {
			sw.write(fmb.build());
			// logger.info("Flow from port {} to port {}, match: {}", pin
			// .getInPort().getPortNumber(), outPort.getPortNumber(), m
			// .toString());
		} catch (Exception e) {
			logger.error("Error: {}", e);
		}
	}

	public static void simpleAddto(IOFSwitch sw, OFPacketIn pin,
			FloodlightContext cntx, OFPort outPort, MacAddress dstMac,
			MacAddress srcMac, IPv4Address srcIp, IPv4Address dstIp) {
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd();
		Match m = createMatchFromPacket(sw, pin.getInPort(), cntx);

		OFActionOutput.Builder aob = sw.getOFFactory().actions().buildOutput();
		List<OFAction> actions = new ArrayList<>();

		OFFactory ofFactory = sw.getOFFactory();
		OFAction setNwDst = ofFactory.actions().buildSetNwDst()
				.setNwAddr(dstIp).build();
		actions.add(setNwDst);

		OFAction setNwSrc = ofFactory.actions().buildSetNwSrc()
				.setNwAddr(srcIp).build();
		actions.add(setNwSrc);

		OFAction setDlDst = ofFactory.actions().buildSetDlDst()
				.setDlAddr(dstMac).build();
		actions.add(setDlDst);

		OFAction setDlSrc = ofFactory.actions().buildSetDlSrc()
				.setDlAddr(srcMac).build();
		actions.add(setDlSrc);

		aob.setPort(outPort);
		aob.setMaxLen(Integer.MAX_VALUE);
		actions.add(aob.build());

		fmb.setMatch(m).setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
				.setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
				.setBufferId(pin.getBufferId()).setOutPort(outPort)
				.setPriority(FLOWMOD_DEFAULT_PRIORITY);

		fmb.setActions(actions);

		try {
			sw.write(fmb.build());
			// logger.info("Flow from port {} to port {}, match: {}", pin
			// .getInPort().getPortNumber(), outPort.getPortNumber(), m
			// .toString());
		} catch (Exception e) {
			logger.error("Error: {}", e);
		}
	}

	public static Match createMatchFromPacket(IOFSwitch sw, OFPort inPort,
			FloodlightContext cntx) {
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
				IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		Match.Builder mb = sw.getOFFactory().buildMatch();
		mb.setExact(MatchField.IN_PORT, inPort);

		if (FLOWMOD_DEFAULT_MATCH_MAC) {
			MacAddress srcMac = eth.getSourceMACAddress();
			MacAddress dstMac = eth.getDestinationMACAddress();
			mb.setExact(MatchField.ETH_SRC, srcMac).setExact(
					MatchField.ETH_DST, dstMac);
		}

		if (FLOWMOD_DEFAULT_MATCH_VLAN) {
			VlanVid vlan = VlanVid.ofVlan(eth.getVlanID());
			if (!vlan.equals(VlanVid.ZERO)) {
				mb.setExact(MatchField.VLAN_VID, OFVlanVidMatch.ofVlanVid(vlan));
			}
		}

		if (FLOWMOD_DEFAULT_MATCH_IP_ADDR && eth.getEtherType() == EthType.IPv4) {
			IPv4 ip = (IPv4) eth.getPayload();
			IPv4Address srcIp = ip.getSourceAddress();
			IPv4Address dstIp = ip.getDestinationAddress();
			mb.setExact(MatchField.ETH_TYPE, EthType.IPv4)
					.setExact(MatchField.IPV4_SRC, srcIp)
					.setExact(MatchField.IPV4_DST, dstIp);

//			if (FLOWMOD_DEFAULT_MATCH_TRANSPORT) {
//				if (ip.getProtocol().equals(IpProtocol.TCP)) {
//					TCP tcp = (TCP) ip.getPayload();
//					mb.setExact(MatchField.IP_PROTO, IpProtocol.TCP)
//							.setExact(MatchField.TCP_SRC, tcp.getSourcePort())
//							.setExact(MatchField.TCP_DST,
//									tcp.getDestinationPort());
//				} else if (ip.getProtocol().equals(IpProtocol.UDP)) {
//					UDP udp = (UDP) ip.getPayload();
//					mb.setExact(MatchField.IP_PROTO, IpProtocol.UDP)
//							.setExact(MatchField.UDP_SRC, udp.getSourcePort())
//							.setExact(MatchField.UDP_DST,
//									udp.getDestinationPort());
//				}
//			}
		} else if (FLOWMOD_DEFAULT_MATCH_IP_ADDR
				&& eth.getEtherType() == EthType.ARP) {
			mb.setExact(MatchField.ETH_TYPE, EthType.ARP);
		}

		return mb.build();
	}

}
