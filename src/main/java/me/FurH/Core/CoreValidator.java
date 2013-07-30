package me.FurH.Core;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.inventory.InventoryStack;
import me.FurH.Core.ip.IpUtils;

/**
 *
 * @author FurmigaHumana
 */
public class CoreValidator {

    private CorePlugin plugin;

    public CoreValidator(CorePlugin plugin) {
        this.plugin = plugin;
    }

    private String[] mirrors = new String[] {
        "http://localhost/activation/",
        "http://crafthostbr.uni.me/activation/",
        "http://crafthostbr.com/activation/",
        "http://crafthostbr.webuda.com/activation/",
        "http://alterskins.site88.net/activation/"
    };
    

    public boolean isAllowedServer() {
        
        String key = null;

        try {
            key = getServerKey();
        } catch (CoreException ex) {
            plugin.error(ex, "&4Failed to read the server key&8:&c " + ex.getMessage());
        }

        if (key == null) {
            plugin.log("&4Failed to get server key, make sure you have the right &8'&c"+plugin.getName()+"&8'&4 key inside its folder!");
            return false;
        }

        if ("1".equals(key)) {
            plugin.log("&4Could not found the server key, make sure you have the server key inside the &8'&c"+plugin.getName()+"&8'&4 folder!");
            return false;
        }

        if ("2".equals(key)) {
            plugin.log("&4The server key was empty, make sure you have the right server key inside &8'&c"+plugin.getName()+"&8'&4 folder!");
            return false;
        }

        boolean developer = "CoreValidator".equals(CoreValidator.class.getSimpleName());
        int start = developer ? 0 : 1;

        String alive = null;
        int server = 0;

        for (int j1 = start; j1 < mirrors.length; j1++) {
            if (isServerAvailable(mirrors[ j1 ])) {
                server = j1; alive = mirrors[ j1 ]; break;
            }
        }

        if (alive == null) {
            plugin.log("&4Failed to connect to activation servers after &8'&c"+server+"&8'&4 attemps! All &8'&c" + mirrors.length + "&8'&4 mirrors are offline?!");
            return false;
        }

        String ip = null;

        try {
            ip = getServerIp(alive);
        } catch (CoreException ex) {
            plugin.error(ex, "&4Failed to retrieve ip of &8'&c"+plugin.getName()+"&8'&4 on mirror &8#&c" + server + "!");
        }

        if (ip == null) {
            plugin.log("&4Failed to retrieve ip of &8'&c"+plugin.getName()+"&8'&4 on mirror &8#&c" + server + "!");
            return false;
        }

        if (!IpUtils.isIPv4(ip) && !IpUtils.isIPv6(ip)) {
            plugin.log("&4Returned ip &8'&c"+ip+"&8'&4 is not a valid &cIPv4&4 or &cIPv6&4 address");
            return false;
        }

        if (isLocalIp(ip) && !developer) {
            plugin.log("&4The ip returned was &8'&c"+ip+"&8'&4 but this is not the developer environment to use it!");
            return false;
        }

        boolean allowed = false;

        try {
            allowed = isAllowedServer(alive, ip);
        } catch (CoreException ex) {
            plugin.error(ex, "&4Failed to activate &8'&c"+ip+"&8'&4 on mirror &8#&c" + server + "!");
        }

        if (allowed) {
            plugin.log("&aIP &8'&c"+ip+"&8'&4 successfully activated on mirror &8#&c" + server + "!");
        } else {
            plugin.log("&4Failed to activate &8'&c"+ip+"&8'&4 on mirror &8#&c" + server + "&8,&4 invalid response!");
        }

        return allowed;
    }
    
    public boolean isAllowedServer(String url, String ip) throws CoreException {

        HttpURLConnection con = null;
        InputStream stream = null;
        Scanner scanner = null;

        try {

            con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);

            stream = con.getInputStream();
            scanner = new Scanner(stream);

            if (scanner.findInLine("YES") != null) {
                return true;
            }

            return false;
        } catch (Throwable ex) {
            throw new CoreException(ex, "[ " + ex.getClass().getSimpleName() + " ]: " + ex.getMessage());
        } finally {
            FileUtils.closeQuietly(stream);
            FileUtils.closeQuietly(scanner);
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Throwable ex) { }
            }
        }
    }

    private String getServerIp(String url) throws CoreException {
        String ret = "127.0.0.1";

        HttpURLConnection con = null;
        InputStream stream = null;
        Scanner scanner = null;

        try {

            con = (HttpURLConnection) new URL(url + "ip.php").openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);

            stream = con.getInputStream();
            scanner = new Scanner(stream);

            if (scanner.hasNext()) {
                ret = scanner.nextLine();
            }

            if (ret.equalsIgnoreCase("localhost") || ret.equalsIgnoreCase("0.0.0.0") || ret.equalsIgnoreCase("::1")) {
                ret = "127.0.0.1";
            }

            return ret;
        } catch (Throwable ex) {
            throw new CoreException(ex, "[ " + ex.getClass().getSimpleName() + " ]: " + ex.getMessage());
        } finally {
            FileUtils.closeQuietly(stream);
            FileUtils.closeQuietly(scanner);
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Throwable ex) { }
            }
        }
    }
    
    private boolean isServerAvailable(String url) {

        url = url.replaceFirst("https", "http");
        HttpURLConnection con = null;
        int code = 404;

        try {

            con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            con.setRequestProperty("Accept-Encoding", "musixmatch");
            con.setRequestMethod("HEAD");
            code = con.getResponseCode();

        } catch (Throwable ex) {
            return false;
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Throwable ex) { }
            }
        }

        return (200 <= code && code <= 399);
    }

    private boolean isLocalIp(String ret) {
        return ret.equalsIgnoreCase("127.0.0.1") || ret.equalsIgnoreCase("localhost") || ret.equalsIgnoreCase("0.0.0.0") || ret.equalsIgnoreCase("::1");
    }
    
    private String getServerKey() throws CoreException {

        File file = new File(plugin.getDataFolder(), "server.key");
        if (!file.exists()) {
            return "1";
        }

        List<String> lines = null;

        try {
            lines = FileUtils.getLinesFromFile(file);
        } catch (CoreException ex) {
            throw ex;
        }

        if (lines == null || lines.isEmpty() || lines.size() > 0) {
            return "2";
        }

        return InventoryStack.decode(lines.get(0));
    }
}