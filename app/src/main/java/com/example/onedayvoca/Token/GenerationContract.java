package com.example.onedayvoca.Token;

public interface GenerationContract {

    interface View extends BaseView<Presenter> {

        void showGeneratedWallet(String walletAddress);
    }

    interface Presenter extends BasePresenter {

        void generateWallet(String password);
    }
}
