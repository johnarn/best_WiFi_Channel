


/*
*	Created by John Arno
*	Created at 21/12/2018
*/
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class best {

    private static ArrayList<String> interfaces = new ArrayList();
    private static String password = "";
    private static String channel;

    public static void main(String[] args) {
        executeCommand("rm interfaces.txt");
        executeCommand("nmcli d | grep wifi >> interfaces.txt");
        findInterfaces();
        executeCommand("rm scan.txt");

        // findChannelByAPs();

        findChannelByUtilization();

    }

    public static void findChannelByUtilization() {

        String interface_number = " ";
        int capture_duration = 10;
        int bitrate = -1;




        System.out.println("\nScanning...");
        executeCommand("rm tshark_interfaces.txt");
        executeCommand("rm captured.pcap");
        executeCommand("tshark -D >> tshark_interfaces.txt");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("tshark_interfaces.txt"));
            String line = bufferedReader.readLine();
            while (line != null) {
                if (line.contains(interfaces.get(0))) {
                    String[] splitted_line = line.split(".");
                    interface_number = splitted_line[0];
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        executeCommand("tshark -i " + interface_number + " -a duration:" + capture_duration + " -w captured.pcap");
        executeCommand("tshark -nr captured.pcap -T fields -e frame.len | sort -n |uniq -c >> sorted_captured.txt");

        System.out.println("Calculating packet size");

        try{
            int packets = 0;
            int packet_size = 0;
            BufferedReader bufferedReader = new BufferedReader(new FileReader("sorted_captured.txt"));
            String line = bufferedReader.readLine();
            while(line != null){
                String[] splitted_line = line.split(" ");
                packets += Integer.parseInt(splitted_line[0]);
                packet_size += Integer.parseInt(splitted_line[1]);
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        executeCommand("iwlist " + interfaces.get(0) + " bitrate >> bitrate.txt");
        try{
            
            BufferedReader bufferedReader = new BufferedReader(new FileReader("bitrate.txt"));
            String line = bufferedReader.readLine();
            while(line != null){
                if(line.contains("Current")){
                    String[] splitted_line = line.split(":");
                    String[] split_line = splitted_line[1].split(" ");
                    bitrate = Integer.parseInt(split_line[0]);

                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        //int utilization = packet_size / 

        System.out.println();

    }

    public static void findChannelByAPs() {
        System.out.println("\nScanning...");
        for (String iface : interfaces) {
            // find the least populated channel (1, 6, 11)
            executeCommand("iwlist " + iface + " scan | grep Frequency | sort | uniq -c | sort -n >> scan.txt");
        }

        boolean channelFound = false;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("scan.txt"));
            String line = bufferedReader.readLine();
            while (line != null) {

                if (!channelFound
                        && (line.contains("Channel 1") || line.contains("Channel 6") || line.contains("Channel 11"))
                        && !(line.contains("Channel 10") || line.contains("Channel 12") || line.contains("Channel 13")
                                || line.contains("Channel 14"))) {
                    channel = line.substring(line.indexOf("Channel") + 7, line.indexOf(")"));
                    channelFound = true;
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\nBest Channel: " + channel);
    }

    public static void executeCommand(String command) {
        String[] arg = new String[] { "/bin/bash", "-c", "echo " + password + " | sudo -S " + command };
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(arg);
            Process proc = processBuilder.start();
            int exitValue = proc.waitFor();
            if (exitValue != 0) {
                System.out.println("Something went wrong with command execution");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void findInterfaces() {
        interfaces.clear();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("interfaces.txt"));
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] splitted_line = line.split(" ");
                interfaces.add(splitted_line[0]);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (interfaces.isEmpty()) {
            System.out.println("No Interfaces Found");
            return;
        }

        System.out.print("Interfaces Found: " + interfaces.get(0));
        boolean isFirst = true;
        for (String iface : interfaces) {
            if (isFirst) {
                isFirst = false;
                continue;
            }
            System.out.print(", " + iface);
        }
        System.out.println();
    }
}
