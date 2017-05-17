package com.jxlc.tajiproject.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 塔吊必备限制器:
 * (1)起重量限制器：也称超载限位器、是一种能使起重机不致超负荷运行的保险装置，当吊重超过额定起重量时，
 * 它能自动地切断提升机构的电源停车或发出警报。起重限制器有机械式和电子式两种。
 *
 * (2)力矩限制器：对于变幅起重机，一定的幅度只允许起吊一定的吊重，如果超重，起重视就有倾翻的危险。
 * 力矩限制器就是根据这个特点研制出的一种保护装置。在某一定幅度，如果吊物超出了其相应的重量，
 * 电路就被切断，使提升不能进行，保证了起重机的稳定。力矩限制器有机械式、电子式和复合式三种。
 *
 * (3)高度限制器：也称吊钩高度限位器．一般都装在起重臂的头部，当吊钩滑升到极限位置，便托起杠杆。
 * 压下限位开关，切断电路停车，再合闸时，吊钩只能下降。
 *
 * (4)行程限制器：防止起重机发生撞车或限制在一定范围内行驶的保险装置。它一般安装在主动台车内侧，
 * 主要是安装一个可以拨动扳把的行程开关。另在轨道的端头（在运行限定的位置）安装一个固定的极限位置挡板，
 * 当塔吊运行到这个位置时，极限挡板即碰触行程开关的扳把，切断控制行走的电源，再合闸时塔吊只能向相反方向运行。
 *
 * (5)回转限制器：保护回转转到设定危险范围内变幅限位器的作用是限制小车出到或回到某处
 * 设定出停止，保护小车不出到或收到一定危险范围内。
 */

public class TowerCraneInfo {
    private List<InfoListener> mListeners;
    private int identifier;          // id
    private String modelName;        // 型号名称

    private float coordinateX;         // x
    private float coordinateY;         // y
    private float frontArmLength;      // 前臂长
    private float rearArmLength;       // 后臂长
    private float armToGroundHeight;   // 塔机高度
    private float trolleyDistance;     // 小车行距
    private float ropeLength;          // 吊绳长度
    private float angle;             // 旋臂角度

    private boolean liftWeightLimiter;  // 起升重量限制器
    private boolean liftHeightLimiter;  // 起升高度限制器
    private boolean torqueLimiter;      // 力矩限制器
    private boolean overstrokeLimiter;  // 行程限制器
    private boolean slewingLimiter;     // 回转限制器

    public TowerCraneInfo(int id, int coordinateX, int coordinateY, int frontArmLength, int rearArmLength,
                          int armToGroundHeight, int trolleyDistance, int ropeLength, float angle) {
        this(id, "ZSL750", coordinateX, coordinateY, frontArmLength, rearArmLength, armToGroundHeight, trolleyDistance,
                ropeLength, angle, true, true, true, true, true);
    }

    public TowerCraneInfo(int id, String modelName, float coordinateX, float coordinateY, float frontArmLength, float rearArmLength,
                          float armToGroundHeight, float trolleyDistance, float ropeLength, float angle, boolean isLiftWeightLimiterWorkFine,
                          boolean isLiftHeightLimiterWorkFine, boolean isTorqueLimiterWorkFine, boolean isOverstrokeLimiterWorkFine, boolean isSlewingLimiterWorkFine) {
        this.identifier = id;
        this.modelName = modelName;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.frontArmLength = frontArmLength;
        this.rearArmLength = rearArmLength;
        this.armToGroundHeight = armToGroundHeight;
        this.trolleyDistance = trolleyDistance;
        this.ropeLength = ropeLength;
        this.angle = angle;
        this.liftWeightLimiter = isLiftWeightLimiterWorkFine;
        this.liftHeightLimiter = isLiftHeightLimiterWorkFine;
        this.torqueLimiter = isTorqueLimiterWorkFine;
        this.overstrokeLimiter = isOverstrokeLimiterWorkFine;
        this.slewingLimiter = isSlewingLimiterWorkFine;
        mListeners = new ArrayList<>();
    }

    public static TowerCraneInfo getDemoInfo() {
        return new TowerCraneInfo(1, "ZSL750", 200, 200, 60, 5, 100, 40, 20, 60, true, true, true, true, true);
    }

    public void addListener(InfoListener l) {
        mListeners.add(l);
    }

    public void removeListener(InfoListener l) {
        mListeners.remove(l);
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
        }
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
        }
    }

    public float getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(float coordinateX) {
        this.coordinateX = coordinateX;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
            l.onStableInfoChanged(identifier);
        }
    }

    public float getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(float coordinateY) {
        this.coordinateY = coordinateY;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
            l.onStableInfoChanged(identifier);
        }
    }

    public float getFrontArmLength() {
        return frontArmLength;
    }

    public void setFrontArmLength(float frontArmLength) {
        this.frontArmLength = frontArmLength;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
            l.onPaintInfoChanged(identifier);
            l.onStableInfoChanged(identifier);
        }
    }

    public float getRearArmLength() {
        return rearArmLength;
    }

    public void setRearArmLength(float rearArmLength) {
        this.rearArmLength = rearArmLength;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
            l.onPaintInfoChanged(identifier);
            l.onStableInfoChanged(identifier);
        }
    }

    public float getTrolleyDistance() {
        return trolleyDistance;
    }

    public void setTrolleyDistance(float trolleyDistance) {
        this.trolleyDistance = trolleyDistance;
        for (InfoListener l : mListeners) {
            l.onPaintInfoChanged(identifier);
            l.onInfoChanged(identifier);
        }
    }

    public float getArmToGroundHeight() {
        return armToGroundHeight;
    }

    public void setArmToGroundHeight(float armToGroundHeight) {
        this.armToGroundHeight = armToGroundHeight;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
        }
    }

    public float getRopeLength() {
        return ropeLength;
    }

    public void setRopeLength(float ropeLength) {
        this.ropeLength = ropeLength;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
        }
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
            l.onPaintInfoChanged(identifier);
        }
    }

    public boolean isLiftWeightLimiterWorkFine() {
        return liftWeightLimiter;
    }

    public void setLiftWeightLimiterWorkStatus(boolean isWorkfine) {
        this.liftWeightLimiter = isWorkfine;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
        }
    }

    public boolean isLiftHeightLimiterWorkFine() {
        return liftHeightLimiter;
    }

    public void setLiftHeightLimiterWorkStatus(boolean isWorkfine) {
        this.liftHeightLimiter = isWorkfine;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
        }
    }

    public boolean isTorqueLimiterWorkFine() {
        return torqueLimiter;
    }

    public void setTorqueLimiterWorkStatus(boolean isWorkfine) {
        this.torqueLimiter = isWorkfine;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
        }
    }

    public boolean isOverstrokeLimiterWorkFine() {
        return overstrokeLimiter;
    }

    public void setOverstrokeLimiterWorkStatus(boolean isWorkfine) {
        this.overstrokeLimiter = isWorkfine;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
        }
    }

    public boolean isSlewingLimiterWorkFine() {
        return slewingLimiter;
    }

    public void setSlewingLimiterWorkStatus(boolean isWorkfine) {
        this.slewingLimiter = isWorkfine;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(identifier);
        }
    }

    @Override
    public String toString() {
        return "TowerCraneInfo{" +
                "identifier=" + identifier +
                ", modelName='" + modelName + '\'' +
                ", coordinateX=" + coordinateX +
                ", coordinateY=" + coordinateY +
                ", frontArmLength=" + frontArmLength +
                ", rearArmLength=" + rearArmLength +
                ", trolleyDistance=" + trolleyDistance +
                ", armToGroundHeight=" + armToGroundHeight +
                ", ropeLength=" + ropeLength +
                ", angle=" + angle +
                ", liftWeightLimiter=" + liftWeightLimiter +
                ", liftHeightLimiter=" + liftHeightLimiter +
                ", torqueLimiter=" + torqueLimiter +
                ", overstrokeLimiter=" + overstrokeLimiter +
                ", slewingLimiter=" + slewingLimiter +
                '}';
    }
}
