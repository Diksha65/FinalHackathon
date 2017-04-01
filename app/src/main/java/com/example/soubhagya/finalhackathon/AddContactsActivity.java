package com.example.soubhagya.finalhackathon;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by soubhagya on 1/4/17.
 */

public class AddContactsActivity extends AppCompatActivity {

    private static final String EXTRA_PHONE_NUM = "com.example.soubhagya.finalhackathon.phoneNum";

    public static Intent newIntent(Context context, String phoneNum){
        Intent i = new Intent(context, AddContactsActivity.class);
        i.putExtra(EXTRA_PHONE_NUM, phoneNum);
        return i;
    }

    private static final int REQUEST_CONTACT = 1;

    private Button mAddContactsButton;
    private Button mNextButton;

    private DataStash dataStash = DataStash.getDataStash();
    
    private String userPhoneNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        userPhoneNum = getIntent().getStringExtra(EXTRA_PHONE_NUM);

        mAddContactsButton = (Button) findViewById(R.id.button_add_contacts);
        mAddContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent pickContact = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        mNextButton = (Button) findViewById(R.id.button_next);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(AddContactsActivity.this, MapsActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Log.e("MainAct:onActResult", "Failed");
            return;
        }

        switch (requestCode) {
            case REQUEST_CONTACT:
                if (data != null) {
                    getTrustedContactAndUpdateFirebase(data);
                }
                break;
        }
    }

    public void getTrustedContactAndUpdateFirebase(Intent data){
        Uri contactUri = data.getData();

        Cursor c = this.getContentResolver().query(contactUri,
                null, null, null, null);

        if (c.getCount() == 0) {
            c.close();
            return;
        }

        if (c.moveToFirst()) {
            String name = c
                    .getString(c
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String trustedContactNum = c
                    .getString(c
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String contactId = c.getString(c
                    .getColumnIndex(ContactsContract.Contacts._ID));
            if (trustedContactNum.equals("1")) {
                Cursor phones = this
                        .getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = " + contactId, null, null);
                while (phones.moveToNext()) {
                    trustedContactNum = phones
                            .getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    notifyUser(trustedContactNum);

                    updateUserNode(trustedContactNum);

                    //updateTrustedContactNode(trustedContactNum);

                }
                phones.close();
            }
            Log.d("Stack",
                    name + " " + " " + trustedContactNum + " "
                            + c.getColumnCount());
        }
        c.close();

    }


    public void updateUserNode(final String trustedContactNum){
        dataStash.fireBase
                .child("USERS")
                .child(userPhoneNum)
                .child("trustedContacts")
                .child(trustedContactNum)
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dataStash.fireBase
                                .child("USERS")
                                .child(trustedContactNum)
                                .child("dependents")
                                .setValue(userPhoneNum);
                    }
                });
    }

    public void updateTrustedContactNode(final String trustedContactNum){
        dataStash.fireBase
                .child("USERS")
                .child(trustedContactNum)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataStash.fireBase
                                    .child("USERS")
                                    .child(trustedContactNum)
                                    .child("dependents")
                                    .setValue(userPhoneNum);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
