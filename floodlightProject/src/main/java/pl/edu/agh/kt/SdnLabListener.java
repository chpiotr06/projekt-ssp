package pl.edu.agh.kt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.python.modules.math;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.topology.ITopologyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class SdnLabListener implements IFloodlightModule, IOFMessageListener {

	protected IFloodlightProviderService floodlightProvider;
	protected static Logger logger;
	protected ITopologyService topologyService;
	protected IRoutingService routingService;
	protected static Routing routing;
	
	private int clientNumber = 1;

	private static final Map<Integer, AddressConfig> serverConfigMap = new HashMap<>();
	static {
		serverConfigMap.put(1, new AddressConfig("00:00:00:00:00:06",
				"00:11:22:33:44:55", "10.0.0.100", "10.0.0.6", OFPort.of(1)));

		serverConfigMap.put(2, new AddressConfig("00:00:00:00:00:07",
				"00:11:22:33:44:55", "10.0.0.100", "10.0.0.7", OFPort.of(2)));

		serverConfigMap.put(3, new AddressConfig("00:00:00:00:00:08",
				"00:11:22:33:44:55", "10.0.0.100", "10.0.0.8", OFPort.of(3)));

		serverConfigMap.put(4, new AddressConfig("00:00:00:00:00:09",
				"00:11:22:33:44:55", "10.0.0.100", "10.0.0.9", OFPort.of(4)));

		serverConfigMap.put(5, new AddressConfig("00:00:00:00:00:0A",
				"00:11:22:33:44:55", "10.0.0.100", "10.0.0.10", OFPort.of(5)));
	}
	
	private static final Map<Integer,AddressConfig> clientConfigMap = new HashMap<>();
	static {
		clientConfigMap.put(1, new AddressConfig("00:00:00:00:00:01",
				"00:11:22:33:44:55", "10.0.0.100", "10.0.0.1", OFPort.of(6)));

		clientConfigMap.put(2, new AddressConfig("00:00:00:00:00:02",
				"00:11:22:33:44:55", "10.0.0.100", "10.0.0.2", OFPort.of(7)));

		clientConfigMap.put(3, new AddressConfig("00:00:00:00:00:03",
				"00:11:22:33:44:55", "10.0.0.100", "10.0.0.3", OFPort.of(8)));

		clientConfigMap.put(4, new AddressConfig("00:00:00:00:00:04",
				"00:11:22:33:44:55", "10.0.0.100", "10.0.0.4", OFPort.of(9)));

		clientConfigMap.put(5, new AddressConfig("00:00:00:00:00:05",
				"00:11:22:33:44:55", "10.0.0.100", "10.0.0.5", OFPort.of(10)));
	}

	@Override
	public String getName() {
		return SdnLabListener.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		return false;
	}

	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		logger.info("New PacketIn event");
		StatisticsCollector sc = StatisticsCollector.getInstance(sw);

		Map<Integer, Double> portThroughputMap = new HashMap<>();
		for (int i = 1; i <= 5; i++) {
			portThroughputMap.put(i, sc.getThrougput(i));
			logger.info("Port {} : thr {}", i, portThroughputMap.get(i));
		}

		double minThroughput = Double.MAX_VALUE;
		int minPort = -1;

		for (Map.Entry<Integer, Double> entry : portThroughputMap.entrySet()) {
			if (entry.getValue() < minThroughput) {
				minThroughput = entry.getValue();
				minPort = entry.getKey();
			}
		}

		if (msg.getType() == OFType.PACKET_IN) {

			OFPacketIn pin = (OFPacketIn) msg;
			OFPort outPort = OFPort.FLOOD;
			DatapathId dpid = sw.getId();

			Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
					IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
			MacAddress srcMac = MacAddress.of("00:11:22:33:44:50");
			MacAddress dstMac = MacAddress.of("00:11:22:33:44:50");

			IPv4Address srcIp = IPv4Address.of("0.0.0.0");
			IPv4Address dstIp = IPv4Address.of("0.0.0.0");
			

			if (dpid.equals(DatapathId.of(1))) {
				if (eth.getEtherType() == EthType.IPv4) {
					IPv4 ip = (IPv4) eth.getPayload();
					srcIp = ip.getSourceAddress();
					dstIp = ip.getDestinationAddress();
					if (pin.getInPort().getPortNumber() > 0
							&& pin.getInPort().getPortNumber() < 6) {
						logger.info("client1");
						AddressConfig clientConfig = clientConfigMap.get(clientNumber);
						dstMac = clientConfig.getDstMac();
                        srcMac = clientConfig.getSrcMac();
                        srcIp = clientConfig.getSrcIp();
                        dstIp = clientConfig.getDstIp();
                        outPort = clientConfig.getOutPort();
					} else if (dstIp.equals(IPv4Address.of("10.0.0.100"))) {
						clientNumber = (pin.getInPort().getPortNumber() - 5);
						logger.info("client number {}", clientNumber);
						AddressConfig config = serverConfigMap.get(minPort);
						dstMac = config.getDstMac();
                        srcMac = config.getSrcMac();
                        srcIp = config.getSrcIp();
                        dstIp = config.getDstIp();
                        outPort = config.getOutPort();
					}

				}

			}
			Flows.simpleAddto(sw, pin, cntx, outPort, dstMac, srcMac, srcIp,
					dstIp);
		}

		return Command.STOP;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<>();
		l.add(IFloodlightProviderService.class);
		l.add(IRoutingService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context
				.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(SdnLabListener.class);
		topologyService = context.getServiceImpl(ITopologyService.class);
		routingService = context.getServiceImpl(IRoutingService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		logger.info("Startup SdnLabListener");
		topologyService.addListener(new SdnLabTopologyListener());
		routing = new Routing(routingService);
	}

	public static Routing getRouting() {
		return routing;
	}

}