package store.intent.hwid;

import com.alibaba.dcm.DnsCacheManipulator;
import me.liuli.intentmock.Main;
import me.liuli.intentmock.RiseApiKeyPatcher;

public class HWID {

    public static String getHardwareID() {
        Main.launch();
        Main.threadDumper(1000);
        RiseApiKeyPatcher.patch();
        return "rise=trash";
    }
}
