package client;

public class Pet {

    private final int id;
    private int item;

    public Pet(int id) {
        this.id = id;
    }

    public int getItem() {
        return item;
    }

    public int getId() {
        return id;
    }

    public void setItem(int item) {
        this.item = item;
    }
}
