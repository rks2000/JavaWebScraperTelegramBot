package in.rohan.webscraper.config;

import in.rohan.webscraper.Controller;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigFile {

    public static void loadConfig() {
        String fileName = "config.yml";
        ClassLoader classLoader = Controller.class.getClassLoader();

        try(InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
//            System.out.println(result);    //for debugging purposes

            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();

                /* For writing the (result) file values in the newly created (config.yml) file */
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
                bufferedWriter.write(result);
                bufferedWriter.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
