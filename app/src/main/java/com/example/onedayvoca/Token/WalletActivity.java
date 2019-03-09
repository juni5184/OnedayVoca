package com.example.onedayvoca.Token;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onedayvoca.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.simple.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;


public class WalletActivity extends AppCompatActivity {

    public static final String TAG = WalletActivity.class.getName();

    private TextView mWalletAddress, mBalance, tBalance;
    private Button btnSendToken, btnGetEther, btnGetToken;

    //generationActivity 에서 생성한 지갑 주소
    private String getAddress;

    private Web3j web3j;
    private JuniToken juniToken;

    private BigInteger GasPrice, GasLimit;

    AlertDialog.Builder errorMsg;
    AlertDialog.Builder confirmationMsg;

    private String buyToken;

    private String gasLimit= String.valueOf(21*1000+21000);
    private String gasPrice= 18+"";

    private String mPriateKey1 = "C4F942D1BB2608F63A05D523684173298DD27E8CF6E33557632DDF7AE890C91C";
    private String mPriateKey2 = "43AE8CD45F3BD31D5300B4906C9E77BE775A5B44D3355463CDDA1D384AC46E93";

    private Credentials credentials;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("이더리움 토큰 지갑");
        toolbar.setTitleTextColor(Color.WHITE);

        getAddress= getIntent().getExtras().getString("WalletAddress");
        web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/0x4602264e9CFcEfafEF6447E846066Ae470DE249D"));

        init();

        mWalletAddress.setText(getAddress+"");
//        mBalance.setText("이더리움 : "+getBalance(getAddress+""));


        if ((getAddress.equals(""))){
            Toast.makeText(WalletActivity.this, "지갑을 생성해주세요", Toast.LENGTH_SHORT).show();
        } else {
            generateQRCode(getAddress);
        }

        //이더리움 충전하기
        btnGetEther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                buyToken("ether");
                buyEther();
            }
        });

        //토큰 충전하기
        btnGetToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                buyToken("token");
                GetFee(gasLimit, gasPrice);
                makeDialog();
            }
        });

        //토큰 전송하기
        btnSendToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(WalletActivity.this, TokenSendActivity.class);
                intent.putExtra("ether",mBalance.getText().toString()+"");
                intent.putExtra("token",tBalance.getText().toString()+"");
                intent.putExtra("WalletAddress", getAddress);
                startActivity(intent);
            }
        });

    }
    /**
     * The value GazLimit and GasPrice converteres in BigInteger and prizhivaetsya global variables
     * calculate the fee for miners
     */

    public void GetFee(String gasLimit, String gasPrice){
        GasPrice = Convert.toWei(gasPrice,Convert.Unit.GWEI).toBigInteger();
        GasLimit = BigInteger.valueOf(Integer.valueOf(gasLimit));

        // fee
        BigDecimal fee = BigDecimal.valueOf(GasPrice.doubleValue()*GasLimit.doubleValue());
        BigDecimal feeresult = Convert.fromWei(fee.toString(),Convert.Unit.ETHER);
//        tv_fee.setText(feeresult.toPlainString() + " ETH");


    }

    @Override
    protected void onResume() {
        super.onResume();
        getErc20Balance();
    }

    public void makeDialog(){
        AlertDialog.Builder ad = new AlertDialog.Builder(WalletActivity.this);

        ad.setTitle("토큰 충전");       // 제목 설정
        ad.setMessage("충전할 토큰의 갯수를 입력하세요.");   // 내용 설정

        // EditText 삽입하기
        final EditText et = new EditText(WalletActivity.this);
        ad.setView(et);

        // 확인 버튼 설정
        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Yes Btn Click");

                // Text 값 받아서 로그 남기기
                buyToken = et.getText().toString();
                Log.v(TAG, buyToken);

                SendingToken st = new SendingToken();
                st.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                dialog.dismiss();     //닫기
                // Event
            }
        });
        // 취소 버튼 설정
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG,"No Btn Click");
                dialog.dismiss();     //닫기
            }
        });

        ad.show();

    }

    private void init() {
        errorMsg = new AlertDialog.Builder(this);
        confirmationMsg = new AlertDialog.Builder(this);

        mWalletAddress = (TextView) findViewById(R.id.account_address);
        mBalance = (TextView) findViewById(R.id.wallet_balance);
        tBalance= (TextView)findViewById(R.id.token_balance);
        btnSendToken =(Button)findViewById(R.id.btnSendToken);
        btnGetEther= (Button)findViewById(R.id.btnGetEther);
        btnGetToken= (Button)findViewById(R.id.btnGetToken);

        /** init Data **/
        //Account 2's private key
        credentials = Credentials.create(mPriateKey1);
        web3j = Web3jFactory.build(new HttpService(Constants.urlEth));
        //Load smart contract
        juniToken = JuniToken.load(Constants.contractAddress, web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);

        //현재 account2 토큰 계좌
        getErc20Balance();
    }

    //이더리움, 토큰 값 불러오기
    private void getErc20Balance() {
        EthGetBalance balance = null;
        BigInteger erc20Balance = null;
        try {
            balance = web3j.ethGetBalance(getAddress+"", DefaultBlockParameterName.LATEST).sendAsync().get();
            erc20Balance = juniToken.balanceOf(getAddress+"").sendAsync().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        mBalance.setText(Convert.fromWei(new BigDecimal(balance.getBalance()), Convert.Unit.ETHER).toPlainString());
//        tBalance.setText(Convert.fromWei(new BigDecimal(erc20Balance), Convert.Unit.ETHER).toPlainString());
        tBalance.setText(erc20Balance.toString());

    }

    private void buyEther(){
        EthereumHandler ethHand = new EthereumHandler();
        ethHand.buyEthers(getAddress, new ApiCallback() {
            @Override
            public void OnSuccess(String result) {
                displayConfirmation("이더리움 충전 완료", "1 이더리움을 충전했습니다!");
            }
            @Override
            public void OnFailure(String message) {
                displayError("Error",message);
            }
        });

    }

    //생성된 지갑의 key 값을 QR 코드로 변환
    public void generateQRCode(String contents) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            Bitmap bitmap = toBitmap(qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, 100, 100));
            ((ImageView) findViewById(R.id.imgQRCode)).setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    public static Bitmap toBitmap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

    private void displayError(String title, String errorMessage){
        errorMsg.setTitle(title);
        errorMsg.setMessage(errorMessage);
        errorMsg.setIcon(R.mipmap.ic_launcher);
        errorMsg.setNeutralButton("Refresh", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int whichButton) {
                d.dismiss();
            }
        });
        errorMsg.show();
    }

    private void displayConfirmation(String title, String errorMessage){
        confirmationMsg.setTitle(title);
        confirmationMsg.setMessage(errorMessage);
        confirmationMsg.setIcon(R.mipmap.ic_launcher);
        confirmationMsg.setNeutralButton("Refresh", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int whichButton) {
                d.dismiss();
                getErc20Balance();
            }
        });
        confirmationMsg.show();
    }


    ///////////////////////// Sending Tokens /////////////////////
    public class SendingToken extends AsyncTask<Void, Integer, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(WalletActivity.this,"토큰 충전"
                    , "토큰 충전중...", true, true);
        }

        @Override
        protected JSONObject doInBackground(Void... param) {

            try {
                /**
                 // Upload the wallet file and get the address
                 */
                Credentials credentials = Credentials.create(mPriateKey2);
//                String address = credentials.getAddress();
                System.out.println("Eth Address: " + getAddress);

                /**
                 * Load Token
                 */
                JuniToken token = JuniToken.load("0x4602264e9CFcEfafEF6447E846066Ae470DE249D", web3j, credentials, GasPrice, GasLimit);

                String status = null;
                String balance = null;

                /**
                 * Convert the amount of tokens to BigInteger and send to the specified address
                 */
                BigInteger sendvalue = BigInteger.valueOf(Long.parseLong(buyToken));
                status = token.transfer(getAddress+"", sendvalue).send().getTransactionHash();

                /**
                 * Renew Token balance
                 */
                BigInteger tokenbalance = token.balanceOf(getAddress).send();
                System.out.println("Balance Token: "+ tokenbalance.toString());
                balance = tokenbalance.toString();

                /**
                 * Returned from thread, transaction Status and Token balance
                 */
                JSONObject result = new JSONObject();
                result.put("status",status);
                result.put("balance",balance);

                return result;
            } catch (Exception ex) {System.out.println("ERROR:" + ex);}

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if (result != null) {
                tBalance.setText(result.get("balance").toString());
                progressDialog.dismiss();
            } else {System.out.println();}
        }
    }
    /////////////////////// End Sending Tokens ///////////////////

}
