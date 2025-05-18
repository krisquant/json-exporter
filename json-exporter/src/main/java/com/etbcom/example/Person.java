package com.etbcom.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.etbcom.export.jsonexport.JSONElement;
import com.etbcom.export.jsonexport.JSONSerializable;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONSerializable(key = "person")
public class Person {
    
    @JSONElement(key = "firstName")
    private String name;
    
    @JSONElement
    private int age;
    
    @JSONElement
    private boolean active;
    
    @JSONElement(key = "contactInfo")
    private ContactInfo contactInfo;
    
    @JSONElement
    private List<String> hobbies;
}