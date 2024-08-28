package ble.solaqua;

public class SelectedDevice {
    String selectedDevice;
    private static final SelectedDevice ourInstance = new SelectedDevice();
    public static SelectedDevice getInstance() {
        return ourInstance;
    }
    private SelectedDevice() { }
    public void setText(String editValue) {
        this.selectedDevice = editValue;
    }
    public String getText() {
        return selectedDevice;
    }
}