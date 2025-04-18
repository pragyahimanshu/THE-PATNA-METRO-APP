import java.io.*;
import java.util.*;
import javax.sound.sampled.*; // For jingle

public class PatnaMetroVoiceApp {

    static boolean isHindi = false;
    static HashMap<String, String> hindiNames = new HashMap<>() {{
        put("Danapur Cantonment", "दानापुर छावनी");
        put("Patna Zoo", "पटना चिड़ियाघर");
        put("Patna Junction", "पटना जंक्शन");
        // Add other station mappings as needed
    }};

    public static void main(String[] args) throws Exception {
        playJingle("metro_jingle.wav"); // Optional metro jingle

        welcomeMessage();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            printMenu();
            if (isHindi) speakHindiMenu();
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(br.readLine());

            switch (choice) {
                case 1:
                    System.out.println("\nAll Patna Metro Stations:");
                    listStations();
                    break;
                case 2:
                    displayMetroMap();
                    break;
                case 3:
                    announceInterchange("Patna Junction");
                    break;
                case 4:
                    isHindi = !isHindi;
                    System.out.println("Language changed to " + (isHindi ? "Hindi" : "English"));
                    break;
                case 5:
                    System.out.println("Thank you for using Patna Metro CLI App ✨");
                    return;
                default:
                    System.out.println("Please choose a valid option.");
            }
        }
    }

    public static void printMenu() {
        System.out.println("===============================");
        System.out.println("        PATNA METRO MENU");
        System.out.println("===============================");
        System.out.println("1. List all stations");
        System.out.println("2. Show the metro map");
        System.out.println("3. Test Interchange Announcement");
        System.out.println("4. Toggle Hindi/English");
        System.out.println("5. Exit");
        System.out.println("-------------------------------");
        System.out.println("Developed by Pragya Himanshu, B.Tech CSE");
        System.out.println("===============================");
    }

    public static void listStations() {
        String[] stations = {
            "Danapur Cantonment", "Saguna Mor", "Patliputra",
            "Patna Zoo", "Patna Junction", "New ISBT"
        };
        for (String s : stations) {
            String label = isHindi ? hindiNames.getOrDefault(s, s) : s;
            System.out.println("- " + label);
        }
    }

    public static void displayMetroMap() {
        System.out.println("\n🗺️ PATNA METRO MAP");
        System.out.println("-------------------------");
        System.out.println("🟥 Red Line: Danapur Cantonment → Patliputra → Patna Zoo → Patna Junction");
        System.out.println("🟦 Blue Line: New ISBT → Patna Junction");
        System.out.println("🔄 Interchange: Patna Junction (Red ↔ Blue)");
    }

    public static void announceInterchange(String station) {
        if (isHindi) {
            speak(station + " पर हैं। आप लाइन बदल सकते हैं।");
        } else {
            speak("You are now at " + station + ". You can change lines here.");
        }
    }

    public static void speakHindiMenu() {
        String msg = "पटना मेट्रो मेनू। एक - स्टेशन सूची। दो - मैप दिखाएँ। तीन - इंटरचेंज घोषणा। चार - भाषा बदलें। पाँच - बाहर निकलें।";
        speak(msg);
    }

    public static void welcomeMessage() {
        if (isHindi) {
            speak("पटना मेट्रो ऐप में आपका स्वागत है।");
        } else {
            speak("Welcome to Patna Metro CLI App.");
        }
    }

    public static void speak(String message) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                String cmd = "powershell -c \"Add-Type –AssemblyName System.Speech;" +
                             "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer;" +
                             "$speak.Speak('" + message + "');\"";
                Runtime.getRuntime().exec(cmd);
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("say \"" + message + "\"");
            } else {
                Runtime.getRuntime().exec("espeak \"" + message + "\"");
            }
        } catch (Exception e) {
            System.out.println("🔇 Audio not supported: " + e.getMessage());
        }
    }

    public static void playJingle(String filename) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(filename));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e) {
            System.out.println("🎶 Can't play jingle: " + e.getMessage());
        }
    }
}
