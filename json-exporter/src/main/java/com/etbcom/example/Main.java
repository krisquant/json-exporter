package com.etbcom.example;

import com.etbcom.export.jsonexport.JSONGenerator;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Create sample data
            Person person1 = new Person(
                "John Doe", 
                30, 
                true, 
                new ContactInfo("john@example.com", "555-1234"),
                Arrays.asList("Reading", "Hiking", "Cooking")
            );
            
            Person person2 = new Person(
                "Jane Smith", 
                25, 
                true, 
                new ContactInfo("jane@example.com", "555-5678"),
                Arrays.asList("Swimming", "Painting")
            );
            
            // Create JSON generator
            JSONGenerator jsonGenerator = new JSONGenerator();
            
            // Convert single object to JSON
            String jsonPerson1 = jsonGenerator.convert(person1);
            System.out.println("=== Testing JSON Exporter ===\n");
            System.out.println("Person as JSON:");
            System.out.println(jsonPerson1);
            System.out.println();
            
            // Save multiple objects to a file
            List<Object> people = Arrays.asList(person1, person2);
            jsonGenerator.saveToFile("people.json", people, "people");
            System.out.println("File saved to people.json");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}