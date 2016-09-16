package com.geniusgithub.mediaplayer.browse;

import com.geniusgithub.mediaplayer.base.BasePresenter;
import com.geniusgithub.mediaplayer.base.BaseView;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import org.cybergarage.upnp.Device;

import java.util.List;

public class BrowseContract {
    public interface IView extends BaseView<IPresenter> {
        public void showProgress(boolean bShow);
        public void showDeviceList(boolean bShow);
        public void showItemList(boolean bShow);
        public void showDeviceDetail(Device device);
        public void updateDeviceList(List<Device> devices);
        public void updateItemList(List<MediaItem>  contentItem);
        public void updateToolTitle(String title);
    }

    public interface IPresenter extends BasePresenter<IView> {
        public void enterDevice(Device device);
        public void browseItem(int index, MediaItem item);
        public void cancelTask();
    }
}
