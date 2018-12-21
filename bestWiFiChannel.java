
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class bestWiFiChannel {

    private static ArrayList<String> interfaces = new ArrayList();     //name of WiFi interface
    private static String password = "";                   //password for sudo command
    private static String channel;                                     //best channel to use
    private static int channel_utilization;                            //channel utilization

    public static void main(String[] args) {

        /**
         * Delete old saved files
         */
        executeCommand("rm interfaces.txt", true);
        executeCommand("rm scan.txt", true);

        /**
         * Find WiFi Interface and add to the interfaces arraylist
         */
        findInterfaces();

        /**
         * Find least populated channel
         */
        findChannelByAPs();

        /**
         * Find channel utilization
         */
        findChannelByUtilization();

    }

    /**
     * Find Channel Utilization using tshark tool
     * Write all the data to files
     */
    public static void findChannelByUtilization() {

        String interface_number = "";   //interface used by tshark
        int capture_duration = 10;      //duration tshark will capture packets
        int bitrate = -1;               //bitrate used by network adapter




        System.out.println("\nScanning...");
        /*
         * Delete all existing files 
         */
        executeCommand("rm tshark_interfaces.txt", true);
        executeCommand("rm captured.pcap", true);
        executeCommand("rm sorted_captured.txt", true);
        executeCommand("rm bitrate.txt", true);

        /*
         * Write the network adapters interfaces to file
         */
        executeCommand("tshark -D >> tshark_interfaces.txt", true);
        /*
         * Read from "tshark_interfaces.txt" the interface number of the WiFi network adapter
         */
         try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("tshark_interfaces.txt"));
            String line = bufferedReader.readLine();
            String[] splitted_line;
            while (line != null) {
                if (line.contains(interfaces.get(0))) {
                    splitted_line = line.split("\\.");
                    interface_number = splitted_line[0];
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("\nGrabbing WiFi traffic...");

        /**
         * Starting to grab network traffic from given interface for a specific duration and saves the data to captured.pcap
         */
        executeCommand("tshark -i " + interface_number + " -a duration:" + capture_duration + " -w captured.pcap", false);

        /**
         * Sleep for the duration tshark need to capture the traffic plus 5 seconds
         */
        try{
            TimeUnit.SECONDS.sleep(capture_duration + 5);

        }catch(InterruptedException e){
            e.printStackTrace();
        }
        

        /**
         * Sort the packets by their number and summs those who has the same size
         */
        executeCommand("tshark -nr captured.pcap -T fields -e frame.len | sort -n |uniq -c >> sorted_captured.txt", false);

        System.out.println("\nCalculating packet size...");
        int packet_count = 0;  //number of packets tshark has captured
        int traffic = 0;        //traffic tshark has captured during the capture duration
        int packet_size = 0;    //the size of all packets

        /**
         * Find packet_count, traffic, packet_size
         */
        try{            
            BufferedReader bufferedReader = new BufferedReader(new FileReader("sorted_captured.txt"));
            String line = bufferedReader.readLine();
            while(line != null){
                String[] splitted_line = line.trim().split(" ");
                traffic += Integer.parseInt(splitted_line[0]) * Integer.parseInt(splitted_line[1]);
                packet_count += Integer.parseInt(splitted_line[0]);
                packet_size += Integer.parseInt(splitted_line[1]);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        }catch(IOException e){
            e.printStackTrace();
        }

        /**
         * Find the bitrate of the WiFi network adapter
         */
        /*executeCommand("iwlist " + interfaces.get(0) + " bitrate >> bitrate.txt", true);
        try{
            
            BufferedReader bufferedReader = new BufferedReader(new FileReader("bitrate.txt"));
            String line = bufferedReader.readLine();
            while(line != null){
                if(line.contains("Current")){
                    String[] splitted_line = line.split(":");
                    String[] split_line = splitted_line[1].split(" ");
                    String[] split = split_line[0].split("\\.");
                    bitrate = Integer.parseInt(split[0]);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        */

        /**
         * Calculcate channel utilization
         */
        channel_utilization = traffic / (packet_size / packet_count);

        System.out.println("\nChannel utilization: " + channel_utilization);

    }


    /**
     * Find the least populated channel based on number of Access Points each channel has
     */
    public static void findChannelByAPs() {
        System.out.println("\nScanning...");

        /**
         * For each WiFi interface count the amount of access points for each channel and sort them by ascending order
         */
        for (String iface : interfaces) {
            executeCommand("iwlist " + iface + " scan | grep Frequency | sort | uniq -c | sort -n >> scan.txt", true);
        }

        boolean channelFound = false;   //best channel has been found

        //find the least populated channel(1, 6, 11) 
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
        /**
         * Print the best channel
         */
        System.out.println("\nBest Channel: " + channel);
    }

    /**
     * Execute given command with sudo(or not) privilages 
     */
    public static void executeCommand(String command, boolean root_privilages) {
        String[] arg;
        if(root_privilages){
            arg = new String[] { "/bin/bash", "-c", "echo " + password + " | sudo -S " + command };
        }else{
            arg = new String[] { "/bin/bash", "-c", command };
        }
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

    /**
     * Initialize Arraylist interfaces with WiFi interfaces
     */
    public static void findInterfaces() {
        interfaces.clear();
        executeCommand("nmcli d | grep wifi >> interfaces.txt", true);
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