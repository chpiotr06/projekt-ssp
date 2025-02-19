package pl.edu.agh.kt;

import java.util.ArrayList;
import java.util.List;

import org.projectfloodlight.openflow.types.DatapathId;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import net.floodlightcontroller.linkdiscovery.ILinkDiscovery;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery.LDUpdate;
import net.floodlightcontroller.topology.ITopologyListener;

public class SdnLabTopologyListener implements ITopologyListener {
	protected static final Logger logger = LoggerFactory
			.getLogger(SdnLabTopologyListener.class);

	@Override
	public void topologyChanged(List<LDUpdate> linkUpdates) {
		// TODO Auto-generated method stub
		for (ILinkDiscovery.LDUpdate update : linkUpdates) {
			switch (update.getOperation()) {
			case LINK_UPDATED:
				logger.debug("Link updated. Update {}", update.toString());
				break;
			case LINK_REMOVED:
				logger.debug("Link removed. Update {}", update.toString());
				break;
			case SWITCH_UPDATED:
				logger.debug("Switch updated. Update {}", update.toString());
				swList.add(update.getSrc());
				SdnLabListener.getRouting().calculateSpfTree(swList);
				break;
			case SWITCH_REMOVED:
				logger.debug("Switch removed. Update {}", update.toString());
				break;
			default:
				break;
			}
		}
	}
	
	private List<DatapathId> swList = new ArrayList<>();
}