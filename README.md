
# Patna Metro Route Finder 🚇

An intelligent voice-assisted metro route finder for the Patna Metro system.

## 👨‍💻 Developed By
**Pragya Himanshu**  
B.Tech CSE Final Year Project

---

## 🎯 Features
- ✅ Find shortest route between stations
- ✅ Calculate distance and time (via Dijkstra's Algorithm)
- ✅ Display fare with student/senior discounts
- ✅ Hindi/English CLI toggle
- ✅ Text-to-Speech voice guidance
- ✅ GUI map viewer with image scroll

---

## 🛠️ Technologies
- Java 8+
- Java Swing (GUI)
- javax.sound.sampled (Audio)
- Graph + Dijkstra Algorithm

---

## 🚀 How to Run
```bash
javac PatnaMetroVoiceApp.java
javac PatnaMetro.java
java PatnaMetroVoiceApp
```

---

## 📂 Files
- `PatnaMetroVoiceApp.java` – CLI logic + voice
- `PatnaMetro.java` – GUI, Dijkstra, fare calculator
- `metro_jingle.wav` – Optional jingle
- `Patna-metro-map.png` – Metro line image
