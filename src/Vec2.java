
public class Vec2 {
    public int x;
    public int y;

    public Vec2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 add(Vec2 other) {
        return new Vec2(this.x + other.x, this.y + other.y);
    }

    public Vec2 subtract(Vec2 other) {
        return new Vec2(this.x - other.x, this.y - other.y);
    }

    public Vec2 rotate(double radian){
        return new Vec2(
            (int)(this.x * Math.cos(radian) - this.y * Math.sin(radian)),
            (int)(this.x * Math.sin(radian) + this.y * Math.cos(radian))
        );
    }

    // 外積
    public int cross(Vec2 other) {
        return this.x * other.y - this.y * other.x;
    }
}
