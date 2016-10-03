package com.geniusgithub.mediaplayer.dlna.proxy;

import com.geniusgithub.mediaplayer.dlna.center.ControlPointImpl;

public interface IControlOperator {
    public  int getControlStatus();
    public  void setControlStauts(int stauts);

    public ControlPointImpl getControlPoint();
    public void setControlPoint(ControlPointImpl controlPoint);

}
