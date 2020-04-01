package client;

import lombok.Data;
import lombok.NonNull;

@Data
public class Pet {

    private @NonNull int id;
    private int item;
}
