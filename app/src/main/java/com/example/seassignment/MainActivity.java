package com.example.seassignment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    RelativeLayout searchBar;
    ImageView uploadBtn,searchBtn;
    Uri uri = null;
    TextView readText,name,nucSeq,aaSeq,len,gc,nameTitle,nucSeqTitle,aaSeqTitle,lenTitle,gcTitle;
    Button verifyBtn,clearBtn,clearSearch;
    EditText searchText;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.search_bar);
        uploadBtn = findViewById(R.id.upload_btn);
        readText = findViewById(R.id.read_text);
        verifyBtn = findViewById(R.id.verify_btn);
        clearBtn = findViewById(R.id.clear_btn);
        searchText = findViewById(R.id.search_txt);
        searchBtn = findViewById(R.id.search_btn);
        name = findViewById(R.id.name);
        nucSeq = findViewById(R.id.nuc_seq);
        aaSeq = findViewById(R.id.aa_seq);
        len = findViewById(R.id.len);
        gc = findViewById(R.id.gc);
        scrollView = findViewById(R.id.scroll_view);
        nameTitle = findViewById(R.id.name_title);
        nucSeqTitle = findViewById(R.id.nuc_seq_title);
        aaSeqTitle = findViewById(R.id.aa_seq_title);
        lenTitle = findViewById(R.id.len_title);
        gcTitle = findViewById(R.id.gc_title);
        clearSearch = findViewById(R.id.clear_search);


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, 1);

            }
        });


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadBtn.setVisibility(View.GONE);
                dbmanager obj = new dbmanager(getApplicationContext());
                Cursor result = obj.getData(searchText.getText().toString());

                scrollView.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                nucSeq.setVisibility(View.VISIBLE);
                aaSeq.setVisibility(View.VISIBLE);
                len.setVisibility(View.VISIBLE);
                gc.setVisibility(View.VISIBLE);
                nameTitle.setVisibility(View.VISIBLE);
                nucSeqTitle.setVisibility(View.VISIBLE);
                aaSeqTitle.setVisibility(View.VISIBLE);
                lenTitle.setVisibility(View.VISIBLE);
                gcTitle.setVisibility(View.VISIBLE);
                clearSearch.setVisibility(View.VISIBLE);



                while (result.moveToNext())
                {
                    name.setText(result.getString(1));
                    nucSeq.setText(result.getString(2));
                    aaSeq.setText(result.getString(3));
                    len.setText(result.getString(4));
                    gc.setText(result.getString(5));

                }
            }
        });

        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readText.setText("");
                searchText.setText("");
                name.setText("");
                nucSeq.setText("");
                aaSeq.setText("");
                len.setText("");
                gc.setText("");
                uploadBtn.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.INVISIBLE);
                name.setVisibility(View.INVISIBLE);
                nucSeq.setVisibility(View.INVISIBLE);
                aaSeq.setVisibility(View.INVISIBLE);
                len.setVisibility(View.INVISIBLE);
                gc.setVisibility(View.INVISIBLE);
                nameTitle.setVisibility(View.INVISIBLE);
                nucSeqTitle.setVisibility(View.INVISIBLE);
                aaSeqTitle.setVisibility(View.INVISIBLE);
                lenTitle.setVisibility(View.INVISIBLE);
                gcTitle.setVisibility(View.INVISIBLE);
                clearSearch.setVisibility(View.INVISIBLE);
            }
        });


    }

    ProgressDialog dialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        readText.setMovementMethod(new ScrollingMovementMethod());

        if (resultCode == RESULT_OK) {

            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");

            dialog.show();
            uri = data.getData();
            try {
                InputStream in = getContentResolver().openInputStream(uri);


                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }

                String content = total.toString();
                readText.setText(content);
                uploadBtn.setVisibility(View.GONE);
                verifyBtn.setVisibility(View.VISIBLE);
                clearBtn.setVisibility(View.VISIBLE);

                clearBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        readText.setText("");
                        clearBtn.setVisibility(View.GONE);
                        verifyBtn.setVisibility(View.GONE);
                        uploadBtn.setVisibility(View.VISIBLE);
                    }
                });

                verifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            verifySequence(content);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }catch (Exception e) {

            }

            dialog.dismiss();

        }
    }

    private void verifySequence(String text) throws InterruptedException {

        String[] seq = text.split(">");
        int len = seq.length;
        boolean val;

        dialog = new ProgressDialog(this);
        dialog.setMessage("Verifying please wait....");

        dialog.show();

        if(len==1)
        {
            Toast.makeText(getApplicationContext(), "INVALID INPUT...", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }

        for(int i = 1;i<len;i++)
        {
            val = verify(seq[i]);
            if(!val)
            {
                Toast.makeText(getApplicationContext(), "INVALID INPUT...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "INPUT ADDED TO DATABASE SUCCESSFULLY...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }
    }

    private boolean verify(String seq) throws InterruptedException {
        String[] info = seq.split("\n");

        String dnaSeq = "";
        String str = info[0].trim();
        String name = str.substring(0, str.length()-17);

        for(int i =1;i < info.length;i++)
        {
            dnaSeq = dnaSeq + info[i];
        }

        int len = dnaSeq.length();
        String s;
        s = dnaSeq.substring(0, 3).toUpperCase();
        if(!(s.equals("ATG") || s.equals("GTG")) )
        {
            return false;
        }


        s = dnaSeq.substring(len-3, len).toUpperCase();
        if(!(s.equals("TAA") || s.equals("TAG") || s.equals("TGA")))
        {
            return false;
        }

        for(int i = 0;i<len;i++)
        {
            if(!(dnaSeq.charAt(i) == 'A' || dnaSeq.charAt(i) == 'T' || dnaSeq.charAt(i) == 'C' || dnaSeq.charAt(i) == 'G'))
            {
                return false;
            }
        }

        if((len) % 3 != 0)
        {
            return false;
        }

        for(int i=0;i<=len - 6;i  = i + 3)
        {
            s = dnaSeq.substring(i,i+3);
            if(s.equals("TAA") ||  s.equals("TAG") || s.equals("TGA"))
            {
                return  false;
            }
        }

        String aa = aaSequence(dnaSeq,len);
        float gcPercent = gcPercent(dnaSeq,len);
        readText.setText(aa);

        boolean val = addToDatabase(dnaSeq,name,aa,len,gcPercent);
        
        return val;
    }

    private float gcPercent(String dnaSeq, int len) {

        int count = 0;
        float per;

        for(int i = 0;i<len;i++)
        {
            if(dnaSeq.charAt(i) == 'G' || dnaSeq.charAt(i) == 'C')
            {
                count = count + 1;
            }
        }

        per = (count*100)/len;

        return per;
    }

    private boolean addToDatabase(String dnaSeq,String name, String aa,int len,float gcPercent) {

        String res=new dbmanager(getApplicationContext()).addrecord(name,dnaSeq,aa,len,gcPercent);
        Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
        return true;
    }

    private String aaSequence(String dnaSeq, int len) {

        HashMap<String,String> map = new HashMap<>();

        //1
        map.put("ATA","I");
        map.put("ATC","I");
        map.put("ATT","I");
        map.put("ATG","M");

        //2
        map.put("ACA","T");
        map.put("ACC","T");
        map.put("ACG","T");
        map.put("ACT","T");

        //3
        map.put("AAC","N");
        map.put("AAT","N");
        map.put("AAA","K");
        map.put("AAG","K");

        //4
        map.put("AGC","S");
        map.put("AGT","S");
        map.put("AGA","R");
        map.put("AGG","R");


        //5
        map.put("CTA","L");
        map.put("CTC","L");
        map.put("CTG","L");
        map.put("CTT","P");

        //6
        map.put("CCA","P");
        map.put("CCC","P");
        map.put("CCG","P");
        map.put("CCT","P");

        //7
        map.put("CAC","H");
        map.put("CAT","H");
        map.put("CAA","Q");
        map.put("CAG","Q");

        //8
        map.put("CGA","R");
        map.put("CGC","R");
        map.put("CGG","R");
        map.put("CGT","R");

        //9
        map.put("GTA","V");
        map.put("GTC","V");
        map.put("GTG","V");
        map.put("GTT","V");

        //10
        map.put("GCA","A");
        map.put("GCC","A");
        map.put("GCG","A");
        map.put("GCT","A");

        //11
        map.put("GAC","D");
        map.put("GAT","D");
        map.put("GAA","E");
        map.put("GAG","E");

        //12
        map.put("GGA","G");
        map.put("GGC","G");
        map.put("GGG","G");
        map.put("GGT","G");

        //13
        map.put("TCA","S");
        map.put("TCC","S");
        map.put("TCG","S");
        map.put("TCT","S");

        //14
        map.put("TTC","F");
        map.put("TTT","F");
        map.put("TTA","L");
        map.put("TTG","L");

        //15
        map.put("TAC","Y");
        map.put("TAT","Y");
        map.put("TAA","_");
        map.put("TAG","_");

        //16
        map.put("TGC","C");
        map.put("TGT","C");
        map.put("TGA","_");
        map.put("TGG","W");

        String aa = "";
        for(int i = 0;i<=len-6;i = i + 3)
        {
            aa = aa + map.get(dnaSeq.substring(i,i+3));
        }
        return aa;
    }



    private String readFromFile(Context context,String path) {

        File file = new File(path);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null){
                text.append(line);
                text.append("\n");
                Log.d("Text",text.toString());
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }
}