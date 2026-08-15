package fr.shuvly.zm.component.window;

public enum WindowState
{

    INTACT(.0),
    SLIGHTLY_DAMAGED(.25),
    DAMAGED(.5),
    HEAVILY_DAMAGED(.75),
    DESTROYED(1);


    private final double progress;


    WindowState(double progress)
    {
        this.progress = progress;
    }


    public double getProgress() { return this.progress; }

    public WindowState getNextDamageState()
    {
        int nextOrdinal = Math.min(this.ordinal() + 1, values().length - 1);
        return values()[nextOrdinal];
    }

    public WindowState getNextRepairState()
    {
        int nextOrdinal = Math.max(this.ordinal() - 1, 0);
        return values()[nextOrdinal];
    }

}
