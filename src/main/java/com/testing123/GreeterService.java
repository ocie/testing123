package com.testing123;

import com.thirdpartylib.StringService;

public class GreeterService {
    StringService stringService = new StringService();

    public void setStringService(StringService stringService) {
        this.stringService = stringService;
    }

    public String makeGreeting(String name) {
        String salutation = "Hello, ";
        String greeting = stringService.contatenateStrings(salutation, name);
        return addSmilie(greeting);
    }

    private String addSmilie(String input) {
        return input.concat(" :)");
    }
}
