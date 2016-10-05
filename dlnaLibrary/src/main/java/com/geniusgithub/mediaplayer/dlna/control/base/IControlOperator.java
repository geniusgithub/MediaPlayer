package com.geniusgithub.mediaplayer.dlna.control.base;

import com.geniusgithub.mediaplayer.dlna.control.ControlPointImpl;

public interface IControlOperator {
    public  int getControlStatus();
    public  void setControlStauts(int stauts);
    public void setControlPoint(ControlPointImpl controlPoint);

}
