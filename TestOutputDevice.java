package me.ancastanoev;

import me.ancastanoev.io.OutputDevice;
import java.util.ArrayList;
import java.util.List;
import me.ancastanoev.io.OutputDevice;

public class TestOutputDevice extends OutputDevice {
    private List<String> messages = new ArrayList<>();

    @Override
    public void writeMessage(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }
}
