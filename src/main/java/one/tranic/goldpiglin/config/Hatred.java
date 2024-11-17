package one.tranic.goldpiglin.config;

public class Hatred {
    private long expirationTime = 25L;
    private long expirationScannerTime = 30L;

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
}
