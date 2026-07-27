package fr.shuvly.zm.map.region;

public interface Region
{

    /**
     * Evaluates if the given coordinates fall inside this region.
     */
    boolean contains(double x, double y, double z);

}
