package ble.solaqua;

public class Navigation {
    String navigation;
    private static final Navigation ourInstance = new Navigation();
    public static Navigation getInstance() {
        return ourInstance;
    }
    private Navigation() { }
    public void setText(String editValue) {
        this.navigation = editValue;
    }
    public String getText() {
        return navigation;
    }
}