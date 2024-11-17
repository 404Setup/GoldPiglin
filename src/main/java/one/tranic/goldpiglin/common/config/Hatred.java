package one.tranic.goldpiglin.common.config;

public class Hatred {
    private long expirationTime = 25L;
    private long expirationScannerTime = 30L;
    private boolean near = false;
    private int nearX = 6;
    private int nearY = 6;
    private int nearZ = 6;
    private boolean canSee = true;
    private boolean nativeCanSee = false;

    public long getExpirationScannerTime() {
        return expirationScannerTime;
    }

    public void setExpirationScannerTime(long expirationScannerTime) {
        this.expirationScannerTime = expirationScannerTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean isNear() {
        return near;
    }

    public void setNear(boolean near) {
        this.near = near;
    }

    public int getNearX() {
        return nearX;
    }

    public void setNearX(int nearX) {
        this.nearX = nearX;
    }

    public int getNearY() {
        return nearY;
    }

    public void setNearY(int nearY) {
        this.nearY = nearY;
    }

    public int getNearZ() {
        return nearZ;
    }

    public void setNearZ(int nearZ) {
        this.nearZ = nearZ;
    }

    public boolean isCanSee() {
        return canSee;
    }

    public void setCanSee(boolean canSee) {
        this.canSee = canSee;
    }

    public boolean isNativeCanSee() {
        return nativeCanSee;
    }

    public void setNativeCanSee(boolean nativeCanSee) {
        this.nativeCanSee = nativeCanSee;
    }
}
