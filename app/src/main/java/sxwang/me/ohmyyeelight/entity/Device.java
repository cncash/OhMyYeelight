package sxwang.me.ohmyyeelight.entity;

import java.util.Scanner;

/**
 * Created by Shaoxing on 21/04/2017.
 */
public class Device {
    private String id;
    private String model = "Yeelight";
    private int firmwareVersion;
    private String location;
    private boolean on;
    private int bright;
    private int colorTemperature;
    private String sourceText;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(int firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public int getBright() {
        return bright;
    }

    public void setBright(int bright) {
        this.bright = bright;
    }

    public int getColorTemperature() {
        return colorTemperature;
    }

    public void setColorTemperature(int colorTemperature) {
        this.colorTemperature = colorTemperature;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSourceText() {
        return sourceText;
    }

    private void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        return id != null ? id.equals(device.id) : device.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static Device parse(String s) {
        Device device = new Device();
        device.setSourceText(s);
        Scanner scanner = new Scanner(s);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int colon = line.indexOf(":");
            if (colon >= 0) {
                String key = line.substring(0, colon);
                String value = line.substring(colon + 2);
                switch (key) {
                    case "id":
                        device.setId(value);
                        break;
                    case "model":
                        device.setModel(value);
                        break;
                    case "location":
                        device.setLocation(value);
                        break;
                    case "fw_version":
                        device.setFirmwareVersion(Integer.parseInt(value));
                        break;
                    case "bright":
                        device.setBright(Integer.parseInt(value));
                        break;
                    case "ct":
                        device.setColorTemperature(Integer.parseInt(value));
                    case "power":
                        device.setOn(value.contains("on"));
                        break;
                    default:
                        break;
                }
            }
        }
        return device;
    }
}
