package pl.edu.agh.kt;

import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import java.util.List;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.U64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Routing {
	private IRoutingService routingService;
	protected static final Logger logger = LoggerFactory
			.getLogger(Routing.class);

	public Routing(IRoutingService routingService) {
		super();
		this.routingService = routingService;
	}

	public void calculateSpfTree(List<DatapathId> swList) {
		logger.info("Calculating SPF tree");
		for (DatapathId srcData : swList) {
			for (DatapathId dstData : swList) {
				if (dstData != srcData) {
					logger.info("Spf: {}", calculateSpf(srcData, dstData)
							.getPath().toString());
				}
			}
		}
	}

	public Route calculateSpf(DatapathId src, DatapathId dst) {
		Route route = routingService.getRoute(src, dst, U64.of(0));
		return route;
	}
}