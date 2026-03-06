package xyz.ravensharp.gc.utils;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ColorUtil {
    public static ArrayList<String> getColors() {
        ArrayList<String> names = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();

        for (Field f : Color.class.getFields()) {
            if (f.getType() == Color.class && f.getName().equals(f.getName().toUpperCase())) {
                try {
                    Color c = (Color) f.get(null);
                    if (seen.add(c.getRGB())) {
                        names.add(f.getName());
                    }
                } catch (IllegalAccessException ignored) {}
            }
        }
        return names;
    }
    
    public static Color getColor(String cstr) {
        if (cstr == null) {
            return null;
        }

        for (Field f : Color.class.getFields()) {
            if (f.getType() == Color.class && f.getName().equals(f.getName().toUpperCase())) {
                if (f.getName().equalsIgnoreCase(cstr)) {
                    try {
                        return (Color) f.get(null);
                    } catch (IllegalAccessException ignored) {
                    }
                }
            }
        }
        return null;
    }
    
    public static Color getMostUsedColor(String input) {
        if (input == null || !input.contains("\u00A7")) return Color.WHITE;

        // Map Minecraft color codes to java.awt.Color
        Map<Character, Color> colorMap = new HashMap<>();
        colorMap.put('0', new Color(0, 0, 0));         // Black
        colorMap.put('1', new Color(0, 0, 170));       // Dark Blue
        colorMap.put('2', new Color(0, 170, 0));       // Dark Green
        colorMap.put('3', new Color(0, 170, 170));     // Dark Aqua
        colorMap.put('4', new Color(170, 0, 0));       // Dark Red
        colorMap.put('5', new Color(170, 0, 170));     // Dark Purple
        colorMap.put('6', new Color(255, 170, 0));     // Gold
        colorMap.put('7', new Color(170, 170, 170));   // Gray
        colorMap.put('8', new Color(85, 85, 85));      // Dark Gray
        colorMap.put('9', new Color(85, 85, 255));     // Blue
        colorMap.put('a', new Color(85, 255, 85));     // Green
        colorMap.put('b', new Color(85, 255, 255));    // Aqua
        colorMap.put('c', new Color(255, 85, 85));     // Red
        colorMap.put('d', new Color(255, 85, 255));    // Light Purple
        colorMap.put('e', new Color(255, 255, 85));    // Yellow
        colorMap.put('f', new Color(255, 255, 255));   // White

        Map<Character, Integer> counts = new HashMap<>();
        char[] chars = input.toCharArray();
        
        // Scan for \u00A7 codes and count occurrences
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '\u00A7') {
                char code = Character.toLowerCase(chars[i + 1]);
                if (colorMap.containsKey(code)) {
                    counts.put(code, counts.getOrDefault(code, 0) + 1);
                }
            }
        }

        // Pick the code with the highest count
        char winner = 'f';
        int max = -1;
        for (Map.Entry<Character, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                winner = entry.getKey();
            }
        }

        return colorMap.getOrDefault(winner, Color.WHITE);
    }
}