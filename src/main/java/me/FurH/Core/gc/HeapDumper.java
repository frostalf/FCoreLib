package me.FurH.Core.gc;

import com.sun.management.HotSpotDiagnosticMXBean;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.management.MBeanServer;
import me.FurH.Core.CorePlugin;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.util.Utils;

/**
 *
 * @author FurmigaHumana All Rights Reserved unless otherwise explicitly stated.
 */
public class HeapDumper {

    private static volatile HotSpotDiagnosticMXBean mxbean;

    public static void dump() {
        File heap = new File(CorePlugin.getHandler().getDataFolder() + File.separator + "heap", "heap-monitor-");
        if (!heap.exists()) { dump(heap); }
    }

    public static void dump(final File file) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("Dumping heap file...");
                _dump(file);
            }
        };

        thread.setDaemon(true);
        thread.start();
    }

    private static void _dump(File file) {

        file.getParentFile().mkdirs();
        
        long usable = file.getParentFile().getUsableSpace();
        
        if (usable < 524288000L) {
            System.out.println("Not enough free space: " + Utils.getFormatedBytes(usable));
            return;
        }
        
        try {

            if (mxbean == null) {

                synchronized (HeapDumper.class) {

                    if (mxbean == null) {
                        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

                        HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server,
                                "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);

                        mxbean = bean;
                    }
                }
            }

            System.out.println("The server is running low on ram, an report about it was saved on the plugin folder!");
            
            mxbean.dumpHeap(file.getAbsolutePath(), true);

            System.out.println("Compressing heap...");
            
            byte[] buffer = new byte[ 1024 ];

            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            FileInputStream in = null;
            
            try {
                
                fos = new FileOutputStream(new File(file.getAbsolutePath() + ".zip"));
                zos = new ZipOutputStream(fos);

                zos.setLevel(Deflater.BEST_COMPRESSION);

                ZipEntry ze = new ZipEntry(file.getName() + ".hprof");
                zos.putNextEntry(ze);

                in = new FileInputStream(file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                zos.closeEntry();
                file.delete();

                System.out.println("Heap file saved at " + file.getAbsolutePath() + ".zip");

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                FileUtils.closeQuietly(fos);
                FileUtils.closeQuietly(zos);
                FileUtils.closeQuietly(in);
            }

        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}