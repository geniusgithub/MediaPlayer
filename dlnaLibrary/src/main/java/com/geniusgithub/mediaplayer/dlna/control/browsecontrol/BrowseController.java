package com.geniusgithub.mediaplayer.dlna.control.browsecontrol;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.upnp.UPnPStatus;
import org.cybergarage.upnp.std.av.player.action.BrowseResult;
import org.cybergarage.upnp.std.av.server.ContentDirectory;
import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;
import org.cybergarage.util.AlwaysLog;
import org.cybergarage.util.Debug;
import org.cybergarage.xml.Node;
import org.cybergarage.xml.Parser;
import org.cybergarage.xml.ParserException;

public class BrowseController implements IBrowseController {

    public static final String TAG = BrowseController.class.getSimpleName();

    @Override
    public boolean browseItem(Device device, String id, ContainerNode rootNode) {

        if (id == null){
            id = "0";
        }

        return browse(device, id, rootNode);
    }



/*    public boolean browse(Device dev, ContainerNode parentNode)
    {
        return browse(dev, "0", parentNode);
    }*/

    private boolean browse(Device dev, String objectId, ContainerNode parentNode)
    {
        return browse(dev, objectId, false, parentNode);
    }

    private boolean browse(Device dev, String objectId, boolean hasBrowseChildNodes, ContainerNode parentNode)
    {
       return browse(parentNode, dev, objectId, hasBrowseChildNodes);
    }

    private boolean browse(ContainerNode parentNode, Device dev, String objectID, boolean hasBrowseChildNodes)
    {
        if (objectID == null)
            return false;

        Node resultNode = browseDirectChildren(dev, objectID, "*", 0, 0, "");
        if (resultNode == null){
            AlwaysLog.e(TAG, "browseDirectChildren  = null");
            return false;
        }

       AlwaysLog.d(TAG, "browseDirectChildren resultNode.toString() = \n" + resultNode.toString());


        BrowseResult browseResult = new BrowseResult(resultNode);
        int nResultNode = 0;
        int nContents = browseResult.getNContentNodes();
        for (int n=0; n<nContents; n++) {
            Node xmlNode = browseResult.getContentNode(n);
            ContentNode contentNode = null;
            if (ContainerNode.isContainerNode(xmlNode) == true) {
                contentNode = new ContainerNode();
            }
            else if (ItemNode.isItemNode(xmlNode) == true)
                contentNode = new ItemNode();
            if (contentNode == null)
                continue;
            contentNode.set(xmlNode);
            parentNode.addContentNode(contentNode);
            contentNode.setParentID(objectID);
            nResultNode++;
            // Add Child Nodes
            if (hasBrowseChildNodes) {
                if (contentNode.isContainerNode() == true) {
                    ContainerNode containerNode = (ContainerNode)contentNode;
                    int childCnt = containerNode.getChildCount();
                    if (0 < childCnt) {
                        String objid = containerNode.getID();
                        browse(containerNode, dev, objid, true);
                    }
                }
            }
        }

        AlwaysLog.i(TAG, "browseDirectChildren count = " + nResultNode);
        return true;
    }


    private Node browseDirectChildren(
            Device dev,
            String objectID,
            String filter,
            int startIndex,
            int requestedCount,
            String sortCaiteria)
    {
        return browse(dev, objectID, org.cybergarage.upnp.std.av.player.action.BrowseAction.BROWSE_DIRECT_CHILDREN, filter, startIndex, requestedCount, sortCaiteria);
    }

    private Node browse(
            Device dev,
            String objectID,
            String browseFlag,
            String filter,
            int startIndex,
            int requestedCount,
            String sortCaiteria)
    {
        AlwaysLog.i(TAG, "browse objectID = " + objectID + ", browseFlag = " + browseFlag);
        if (dev == null)
            return null;

            Service conDir = dev.getService(ContentDirectory.SERVICE_TYPE);
            if (conDir == null)
                return null;
            Action action = conDir.getAction(ContentDirectory.BROWSE);
            if (action == null)
                return null;

            org.cybergarage.upnp.std.av.player.action.BrowseAction browseAction = new org.cybergarage.upnp.std.av.player.action.BrowseAction(action);
            browseAction.setObjectID(objectID);
            browseAction.setBrowseFlag(browseFlag);
            browseAction.setStartingIndex(startIndex);
            browseAction.setRequestedCount(requestedCount);
            browseAction.setFilter(filter);
            browseAction.setSortCriteria(sortCaiteria);
            if (browseAction.postControlAction() == false) {
                UPnPStatus err = action.getControlStatus();
                AlwaysLog.e(TAG, "Error Code = " + err.getCode());
                AlwaysLog.e(TAG, "Error Desc = " + err.getDescription());
                return null;
            }


		/*
		 * ContentDirectory:1 Service Template Version 1.01
		 * 2.7.4.2. Argument Descriptions
		 *  RequestedCount ui4 Requested number of entries under the object specified by ObjectID.
		 *  RequestedCount =0 indicates request all entries.
		 * Added to set the RequestedCount parameter using the NumberReturned result when the specified parameter is zero and
		 * the NumberReturned parameter is less than the TotalMatches parameter for XMBC.
		*/
            if (requestedCount == 0) {
                int numberReturned = browseAction.getNumberReturned();
                int totalMatches = browseAction.getTotalMatches();
                if (numberReturned == 0) {
                    if (0 < totalMatches) {
                        browseAction.setRequestedCount(totalMatches);
                        if (browseAction.postControlAction() == false)
                            return null;
                    } else {
                        browseAction.setRequestedCount(9999);
                        if (browseAction.postControlAction() == false)
                            return null;
                    }
                }
            }

            Argument resultArg = browseAction.getArgument(org.cybergarage.upnp.std.av.player.action.BrowseAction.RESULT);
            if (resultArg == null)
                return null;

            String resultStr = resultArg.getValue();
            if (resultStr == null)
                return null;

            Node node = null;

            Parser xmlParser = UPnP.getXMLParser();

            try {
                node = xmlParser.parse(resultStr);
            } catch (ParserException pe) {
                Debug.warning(pe);
                return null;
            }


            return node;
    }
}
