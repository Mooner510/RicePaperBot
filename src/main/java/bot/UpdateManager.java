package bot;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class UpdateManager {
    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        try(
                FileReader rw = new FileReader("src/main/resources/학교기본정보_2023년01월31일기준.csv", StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader( rw )
        ) {
            String readLine;
            while ((readLine = br.readLine()) != null) {
                System.out.println(readLine);
                String[] split = readLine.split(",");
                strings.add(split[3] + "," + split[0] + "," + split[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        new File("src/main/resources", "schools.txt").deleteOnExit();

        try (
                FileWriter fw = new FileWriter(new File("src/main/resources", "schools.txt"), StandardCharsets.UTF_8, true);
                BufferedWriter bw = new BufferedWriter(fw);
        ) {
            for (String s : strings) {
                bw.write(s);
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
//
//        File f = new File("coding532.txt");
//        if (f.isFile()) {
//            System.out.println("coding532.txt 파일이 있습니다.");
//        }
//
//        try(
//                FileReader rw = new FileReader("coding532.txt");
//                BufferedReader br = new BufferedReader( rw );
//        ) {
//            String readLine;
//            while( ( readLine =  br.readLine()) != null ){
//                System.out.println(readLine);
//            }
//        }catch ( IOException e ) {
//            e.printStackTrace();
//        }
    }
}
