package me.FurH.Core.configuration;

import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import me.FurH.Core.file.FileUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ConfigLoader extends YamlConfiguration {

    @Override
    public void load(File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
        Validate.notNull(file, "File cannot be null");

        load(new FileInputStream(file));
    }

    @Override
    public void load(InputStream stream) throws IOException, InvalidConfigurationException {
        Validate.notNull(stream, "Stream cannot be null");

        Charset utf = Charset.forName("UTF-8");

        InputStreamReader reader = new InputStreamReader(stream, utf);
        StringBuilder builder = new StringBuilder();
        BufferedReader input = new BufferedReader(reader);

        try {

            String line;

            while ((line = input.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }

        } finally {
            FileUtils.closeQuietly(input);
            FileUtils.closeQuietly(reader);
        }

        loadFromString(builder.toString());
    }

    @Override
    public void save(File file) throws IOException {
        Validate.notNull(file, "File cannot be null");

        Charset utf = Charset.forName("UTF-8");

        Files.createParentDirs(file);
        String data = saveToString();

        FileOutputStream stream = new FileOutputStream(file);
        Writer writer = new OutputStreamWriter(stream, utf);

        try {
            writer.write(data);
        } finally {
            FileUtils.closeQuietly(writer);
            FileUtils.closeQuietly(stream);
        }
    }
}
