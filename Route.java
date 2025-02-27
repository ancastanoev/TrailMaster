package me.ancastanoev;

import java.util.Objects;

public class Route {
    private String name;
    private String difficultyLevel;
    private String mountainName;


    public Route(String name, String difficultyLevel, String mountainName) {
        this.name = name;
        this.difficultyLevel = difficultyLevel;
        this.mountainName = mountainName;
    }


    public String getName() {
        return name;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public String getMountainName() {
        return mountainName;
    }

    // toText
    public String toText() {
        return String.format("%s,%s,%s", name, difficultyLevel, mountainName);
    }

    // fromText
    public static Route fromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Route text cannot be null or empty.");
        }

        String[] parts = text.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid Route data format. Expected format: 'name,difficultyLevel,mountainName'");
        }

        String name = parts[0].trim();
        String difficultyLevel = parts[1].trim();
        String mountainName = parts[2].trim();

        return new Route(name, difficultyLevel, mountainName);
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return Objects.equals(name, route.name) &&
                Objects.equals(difficultyLevel, route.difficultyLevel) &&
                Objects.equals(mountainName, route.mountainName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, difficultyLevel, mountainName);
    }

    public Object getMountainame() {return mountainName;}

    public String getDifficulty() { return difficultyLevel; }

    public void setMountainname(String name) {this.mountainName = mountainName;}

    public void setName(String updatedRoute) { this.name=updatedRoute; }

    public void setDifficultyLevel(String moderate) { difficultyLevel=this.difficultyLevel; }

    public void setMountainName(String updatedMountain) { updatedMountain=this.mountainName; }

}




