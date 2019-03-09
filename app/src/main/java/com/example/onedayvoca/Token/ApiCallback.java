package com.example.onedayvoca.Token;

/**
 * Created by alex on 01/09/2018.
 */

public interface ApiCallback {
    void OnSuccess(String result);
    void OnFailure(String message);

}
