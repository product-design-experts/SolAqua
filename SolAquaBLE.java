package ble.solaqua;

public class SolAquaBLE {
    // UUID / Characteristic list
    // 00002A19-0000-1000-8000-00805f9b34fb (Battery Level) (R)
    // 00002A6E-0000-1000-8000-00805f9b34fb (Temp Reading) (R)
    // 0000fe40-8e22-4541-9d4c-21edae82ed19 (Delta Temp) (R, W)
    // 0000fe41-8e22-4541-9d4c-21edae82ed19 (Max Water Temp) (R, W)
    // 0000fe42-8e22-4541-9d4c-21edae82ed19 (Dwell Time) (R, W)
    // 0000fe43-8e22-4541-9d4c-21edae82ed19 (Pump On Time) (R, W)

    // Services
    public static final String gatt_service = "00001801-0000-1000-8000-00805f9b34fb";
    public static final String user_data = "000181C-0000-1000-8000-00805f9b34fb";
    public static final String scan_params = "00001813-0000-1000-8000-00805f9b34fb";
    // Characteristics
    public static final String gatt_char = "00002a05-0000-1000-8000-00805f9b34fb";
    public static final String battery_level = "00002a19-0000-1000-8000-00805f9b34fb";
    public static final String temp_readings = "00002a6e-0000-1000-8000-00805f9b34fb";
    public static final String delta_temp = "0000fe40-8e22-4541-9d4c-21edae82ed19";
    public static final String max_water_temp = "0000fe41-8e22-4541-9d4c-21edae82ed19";
    public static final String dwell_time = "0000fe42-8e22-4541-9d4c-21edae82ed19";
    public static final String pump_on_time = "0000fe43-8e22-4541-9d4c-21edae82ed19";
    // Descriptors
    public static final String gatt_descriptor = "00002902-0000-1000-8000-00805f9b34fb";
}