package client.interaction;

import client.Character;
import client.Client;

public interface Interactable {

    void open(Character chr);
    void close(Client c);
}
