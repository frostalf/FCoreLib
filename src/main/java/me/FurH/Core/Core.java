package me.FurH.Core;


/**
 *
 * @author FurmigaHumana
 */
public class Core extends CorePlugin {

    public static long start = 0;

    public Core() {
        super("&8[&3CoreLib&8]&7:&f");
    }

    @Override
    public void onEnable() {
        logEnable();
    }

    @Override
    public void onDisable() {
        logDisable();
    }
}