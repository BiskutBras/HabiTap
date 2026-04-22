package model;

public class Goal {
    private int id;
    private String name;
    private String color; // hex like #2563eb

    public Goal(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }

    public void setName(String name) { this.name = name; }
    public void setColor(String color) { this.color = color; }
}

