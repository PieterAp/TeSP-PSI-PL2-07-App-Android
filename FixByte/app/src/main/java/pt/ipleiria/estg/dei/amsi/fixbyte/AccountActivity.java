package pt.ipleiria.estg.dei.amsi.fixbyte;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pt.ipleiria.estg.dei.amsi.fixbyte.listeners.UserListener;
import pt.ipleiria.estg.dei.amsi.fixbyte.modelo.FixByteSingleton;
import pt.ipleiria.estg.dei.amsi.fixbyte.modelo.User;
import pt.ipleiria.estg.dei.amsi.fixbyte.utils.FixByteJsonParser;

public class AccountActivity extends AppCompatActivity implements UserListener {

    private Context context;
    private LayoutInflater inflater;

    private EditText mUsernameView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mDateOfBirthView;
    private EditText mAddressView;
    private EditText mPasswordView;

    private ArrayList<User> user;
    private DatePickerDialog.OnDateSetListener mDateOfBirthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account);

        mUsernameView = (EditText) findViewById(R.id.username);
        mFirstNameView = (EditText) findViewById(R.id.first_name);
        mLastNameView = (EditText) findViewById(R.id.last_name);
        mDateOfBirthView = (EditText) findViewById(R.id.dateOfBirth);

        mDateOfBirthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AccountActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateOfBirthListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateOfBirthListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                //Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date =  day + "/" + month + "/" + year;
                mDateOfBirthView.setText(date);
            }
        };

        mAddressView = (EditText) findViewById(R.id.address);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptToEdit();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.btnSave);
        mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptToEdit();
            }
        });

        load();

    }

    private void load(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String Token = preferences.getString("token", "");

        FixByteSingleton.getInstance(getApplicationContext()).setUserListener(this);
        FixByteSingleton.getInstance(getApplicationContext()).APIgetAccount(getApplicationContext(),FixByteJsonParser.isConnectedInternet(getApplicationContext()),Token);

        if (user != null){
            try {
                Button mEmailSignUpButton = (Button) findViewById(R.id.btnSave);
                mEmailSignUpButton.setVisibility(View.INVISIBLE);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(user.get(0).getUserDataNasc());
                sdf.applyPattern("yyyy/MM/dd");

                SimpleDateFormat origin = new SimpleDateFormat("yyyy/MM/dd");
                Date dateOrigin = origin.parse(sdf.format(date));
                sdf.applyPattern("dd/MM/yyyy");

                mUsernameView.setText(user.get(0).getUsername());
                mFirstNameView.setText(user.get(0).getUserNomeProprio());
                mLastNameView.setText(user.get(0).getUserApelido());
                mDateOfBirthView.setText(sdf.format(dateOrigin));
                mAddressView.setText(user.get(0).getUserMorada());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * Attempts to register the account specified in the form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual account is created.
     */
    private void attemptToEdit()
    {
        // Reset errors.
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String dateString = mDateOfBirthView.getText().toString();
        String address = mAddressView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Password Validation
        if (password.isEmpty()){

        }else{
            if (password.length() < 6) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }
        }

        if (firstName.length()<3){
            mFirstNameView.setError("First name is to short.");
            focusView = mFirstNameView;
            cancel = true;
        }
        if (lastName.length()<3){
            mLastNameView.setError("Last name is to short.");
            focusView = mLastNameView;
            cancel = true;
        }
        if (address.length()<5){
            mAddressView.setError("Address is to short.");
            focusView = mAddressView;
            cancel = true;
        }

        try {
            //date comes in DD/MM/YYYY - String
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(dateString); // convert string to date
            sdf.applyPattern("yyyy/MM/dd"); // convert to yyyy/mm/dd

            //convert to origin YYYY-MM-DD
            SimpleDateFormat origin = new SimpleDateFormat("yyyy/MM/dd");
            Date dateOrigin = origin.parse(sdf.format(date)); // convert string to date
            sdf.applyPattern("yyyy-MM-dd");// convert to yyyy-mm-dd
            //sdf.format(dateOrigin) convert date to string

            int age = getAge(sdf.format(dateOrigin)); // send string, get int
            System.out.println("converted age2 " +age);
            if (age<12){
                mDateOfBirthView.setError("You must be that least 12 years old.");

                Context contexto = getApplicationContext();
                Toast.makeText(contexto, "You must be that least 12 years old.", Toast.LENGTH_SHORT).show();

                focusView = mDateOfBirthView;
                cancel = true;
            }

        } catch (ParseException e) {
            mDateOfBirthView.setError("Something went wrong.");
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            focusView = mDateOfBirthView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            editAPI(firstName,lastName,dateString,address,password);
        }
    }

    private void editAPI(String firstName,String lastName,String dateString,String address,String password){
        FixByteSingleton.getInstance(getApplicationContext()).setUserListener(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String Token = preferences.getString("token", "");

        FixByteSingleton.getInstance(getApplicationContext()).APIEditAccount(getApplicationContext(),FixByteJsonParser.isConnectedInternet(getApplicationContext()),Token,firstName,lastName,dateString,address,password);

    }

    private int getAge(String birthday){
        try{

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(birthday);
            System.out.println(format.format(date));

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.get(Calendar.YEAR);

            return Calendar.getInstance().get(Calendar.YEAR)-calendar.get(Calendar.YEAR);

        } catch (ParseException e) {
            return 0;
        }

    }
    @Override
    public void onRefreshListaUser(ArrayList<User> userdata) {
        this.user = userdata;

        if (mUsernameView != null){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(user.get(0).getUserDataNasc());
                sdf.applyPattern("yyyy/MM/dd");

                SimpleDateFormat origin = new SimpleDateFormat("yyyy/MM/dd");
                Date dateOrigin = origin.parse(sdf.format(date));
                sdf.applyPattern("dd/MM/yyyy");

                mUsernameView.setText(user.get(0).getUsername());
                mFirstNameView.setText(user.get(0).getUserNomeProprio());
                mLastNameView.setText(user.get(0).getUserApelido());
                mDateOfBirthView.setText(sdf.format(dateOrigin));
                mAddressView.setText(user.get(0).getUserMorada());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
