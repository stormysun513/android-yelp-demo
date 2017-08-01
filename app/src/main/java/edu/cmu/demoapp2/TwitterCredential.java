package edu.cmu.demoapp2;

import java.io.Serializable;

/**
 * Created by Yu-Lun Tsai on 01/08/2017.
 */

public class TwitterCredential implements Serializable {

    String accessToken;
    String accessSecret;

    public TwitterCredential(String token, String secret){
        accessToken = token;
        accessSecret = secret;
    }
}
