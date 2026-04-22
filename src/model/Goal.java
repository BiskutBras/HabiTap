package model;

public class Goal {
    private int id;
    private String name;
    private String color; // hex like #2563eb
    private int userId;

    public Goal(int id, String name, String color, int userId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.userId = userId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public int getUserId(){ return userId; }

    public void setName(String name) { this.name = name; }
    public void setColor(String color) { this.color = color; }
    public void setUserId(int userId) { this.userId = userId; }
}

