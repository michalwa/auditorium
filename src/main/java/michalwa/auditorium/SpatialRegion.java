package michalwa.auditorium;

import java.awt.Color;

class SpatialRegion<TData> {
    public float centerX, centerY, radius;
    public Color color = Color.GREEN;
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
