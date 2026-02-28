package michalwa.auditorium;

import java.awt.FileDialog;

import javax.sound.sampled.Clip;

class SpatialRegion<TData> {
    public float centerX, centerY, radius;
    private TData data;

    SpatialRegion(float centerX, float centerY, float radius, TData data) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.data = data;
    }

    public TData getData() {
        return data;
    }
}
