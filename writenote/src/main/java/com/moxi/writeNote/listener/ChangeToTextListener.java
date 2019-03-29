package com.moxi.writeNote.listener;

public interface ChangeToTextListener {
    void onChangeFaile(String error);
    void onChangeSucess(String value);
    void onChangeStart();
}
