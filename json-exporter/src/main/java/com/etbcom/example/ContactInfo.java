package com.etbcom.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.etbcom.export.jsonexport.JSONElement;
import com.etbcom.export.jsonexport.JSONSerializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONSerializable
public class ContactInfo {
    @JSONElement
    private String email;
    
    @JSONElement
    private String phone;
}