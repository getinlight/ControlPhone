package com.getinlight.controlphone.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.SpUtil;
import com.getinlight.controlphone.utils.ToastUtil;

import java.lang.reflect.Array;
import java.util.HashMap;

public class Setup3Activity extends AppCompatActivity {

    private static final int PICK_CONTACT_REQUEST = 0;

    private EditText et_phone_num;
    private Button btn_contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        et_phone_num = findViewById(R.id.et_phone_num);
        btn_contacts = findViewById(R.id.btn_contacts);
        btn_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });

        et_phone_num.setText(SpUtil.getString(this, ConstantValue.CONTACT_PHONE, ""));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            String phoneNumber = "";
            Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
            if (cursor.moveToNext()) {
                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (hasPhone.equalsIgnoreCase("1")) {
                    hasPhone = "true";
                } else {
                    hasPhone = "false";
                }
                if (Boolean.parseBoolean(hasPhone)) {
                    Cursor phones = getContentResolver()
                            .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + id, null, null);
                    while (phones.moveToNext()) {
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phones.close();
                }
                cursor.close();
                phoneNumber = phoneNumber.replaceAll(" ", "").replaceAll("-", "");
                et_phone_num.setText(phoneNumber);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void prePage(View v) {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);

        finish();
    }

    public void nextPage(View v) {
        String phone = et_phone_num.getText().toString().replaceAll(" ", "");
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(this, "您没有设置联系人手机号");
        } else {
            SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);
            finish();
        }
    }

}
