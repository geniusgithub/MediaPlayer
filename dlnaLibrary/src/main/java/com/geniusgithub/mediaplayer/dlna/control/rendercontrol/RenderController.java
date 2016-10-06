package com.geniusgithub.mediaplayer.dlna.control.rendercontrol;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.std.av.renderer.AVTransport;

public class RenderController implements IRenderController {

    @Override
    public boolean setAVTransportURI(Device dev, String path)
    {
        if (path == null || path.length() <= 0)
            return false;

        Service avTransService = dev.getService(AVTransport.SERVICE_TYPE);
        if (avTransService == null)
            return false;

        Action action = avTransService.getAction(AVTransport.SETAVTRANSPORTURI);
        if (action == null)
            return false;

        action.setArgumentValue(AVTransport.INSTANCEID, "0");
        action.setArgumentValue(AVTransport.CURRENTURI, path);
        action.setArgumentValue(AVTransport.CURRENTURIMETADATA, "");

        return action.postControlAction();
    }


    @Override
    public boolean play(Device device) {

        Service avTransService = device.getService(AVTransport.SERVICE_TYPE);
        if (avTransService == null)
            return false;

        Action action = avTransService.getAction(AVTransport.PLAY);
        if (action == null)
            return false;

        action.setArgumentValue(AVTransport.INSTANCEID, "0");
        action.setArgumentValue(AVTransport.SPEED, "1");

        return action.postControlAction();
    }


    @Override
    public boolean pause(Device mediaRenderDevice) {

        Service service = mediaRenderDevice.getService(AVTransport.SERVICE_TYPE);
        if (service == null) {
            return false;
        }
        final Action pauseAction = service.getAction(AVTransport.PAUSE);
        if (pauseAction == null) {
            return false;
        }
        pauseAction.setArgumentValue(AVTransport.INSTANCEID, 0);
        return pauseAction.postControlAction();
    }



    @Override
    public boolean stop(Device device) {

        Service avTransService = device.getService(AVTransport.SERVICE_TYPE);
        if (avTransService == null)
            return false;

        Action action = avTransService.getAction(AVTransport.STOP);
        if (action == null)
            return false;

        action.setArgumentValue(AVTransport.INSTANCEID, "0");

        return action.postControlAction();

    }


    @Override
    public String getPositionInfo(Device device) {
        Service localService = device.getService(AVTransport.SERVICE_TYPE);

        if (localService == null)
            return null;

        final Action localAction = localService.getAction(AVTransport.GETPOSITIONINFO);
        if (localAction == null) {
            return null;
        }

        localAction.setArgumentValue(AVTransport.INSTANCEID, "0");
        boolean isSuccess = localAction.postControlAction();
        if (isSuccess) {
            return localAction.getArgumentValue(AVTransport.ABSTIME);
        } else {
            return null;
        }
    }

    @Override
    public String getMediaDuration(Device device) {
        Service localService = device.getService(AVTransport.SERVICE_TYPE);
        if (localService == null) {
            return null;
        }

        final Action localAction = localService.getAction(AVTransport.GETMEDIAINFO);
        if (localAction == null) {
            return null;
        }

        localAction.setArgumentValue(AVTransport.INSTANCEID, "0");
        if (localAction.postControlAction()) {
            return localAction.getArgumentValue(AVTransport.MEDIADURATION);
        } else {
            return null;
        }

    }



    @Override
    public String getTransportState(Device device) {
        Service localService = device.getService(AVTransport.SERVICE_TYPE);
        if (localService == null) {
            return null;
        }

        final Action localAction = localService.getAction(AVTransport.GETTRANSPORTINFO);
        if (localAction == null) {
            return null;
        }

        localAction.setArgumentValue(AVTransport.INSTANCEID, "0");

        if (localAction.postControlAction()) {
            return localAction.getArgumentValue(AVTransport.CURRENTTRANSPORTSTATE);
        } else {
            return null;
        }
    }




    @Override
    public boolean seek(Device device, String targetPosition) {
        Service localService = device.getService(AVTransport.SERVICE_TYPE);
        if (localService == null)
            return false;

        Action localAction = localService.getAction(AVTransport.SEEK);
        if (localAction == null) {
            return false;
        }
        localAction.setArgumentValue(AVTransport.INSTANCEID, "0");
        // if (mUseRelTime) {
        // localAction.setArgumentValue("Unit", "REL_TIME");
        // } else {
        localAction.setArgumentValue(AVTransport.UNIT, AVTransport.ABSTIME);
        // }

        localAction.setArgumentValue(AVTransport.TARGET, targetPosition);
        boolean postControlAction = localAction.postControlAction();
        if (!postControlAction) {
            localAction.setArgumentValue(AVTransport.UNIT, AVTransport.RELTIME);
            localAction.setArgumentValue(AVTransport.TARGET, targetPosition);
            return localAction.postControlAction();
        } else {
            return postControlAction;
        }

    }


   /* @Override
    public boolean goon(Device device, String pausePosition) {

        Service localService = device.getService(AVTransport1);
        if (localService == null)
            return false;

        final Action localAction = localService.getAction("Seek");
        if (localAction == null)
            return false;
        localAction.setArgumentValue("InstanceID", "0");
        // if (mUseRelTime) {
        // } else {
        // localAction.setArgumentValue("Unit", "ABS_TIME");
        // }
        // LogUtil.e(tag, "继续相对时间："+mUseRelTime);
        // 测试解决播放暂停后时间不准确
        localAction.setArgumentValue("Unit", "ABS_TIME");
        localAction.setArgumentValue("Target", pausePosition);
        localAction.postControlAction();

        Action playAction = localService.getAction("Play");
        if (playAction == null) {
            return false;
        }

        playAction.setArgumentValue("InstanceID", 0);
        playAction.setArgumentValue("Speed", "1");
        return playAction.postControlAction();
    }*/
}
