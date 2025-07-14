package axtonfn.ml.DataManage;
import java.io.*;
import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String FILE_NAME = "mymod_data.txt";

    // 保存多个数据，每个数据占一行
    public static void writeData(String[] data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();  // 每条数据写入一行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] readData() {
        // 读取数据，每行作为一个元素存储在数组中
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines.toArray(new String[0]);
    }
}
