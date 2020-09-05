package client.player;

public class Beauty {

    private final int id, gender;
    private boolean enabled;

    public Beauty(int id, int gender, boolean enabled) {
        this.id = id;
        this.gender = gender;
        this.enabled = enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public int getGender() {
        return gender;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "Beauty{" +
                "id=" + id +
                ", gender=" + gender +
                ", enabled=" + enabled +
                '}';
    }
}
