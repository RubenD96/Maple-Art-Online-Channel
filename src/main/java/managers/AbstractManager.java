package managers;

import util.packet.PacketReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class AbstractManager {

    static PacketReader getData(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return new PacketReader().next(Files.readAllBytes(file.toPath()));
            } else {
                System.err.println("File does not exist (" + path + ")");
                return null;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return new PacketReader().next(new byte[]{});
    }
}