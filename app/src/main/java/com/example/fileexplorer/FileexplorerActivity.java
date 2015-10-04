package com.example.fileexplorer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;




public class FileexplorerActivity extends Activity {

    private static final int REQUEST_PATH = 1;
    private static final String SIGNATURE_PNG = "89504E470D0A1A0A";
    private static final String SIGNATURE_JPEG = "FFD8FF";
    private static final String SIGNATURE_GIF = "474946";
    private static final String TAG = "Krishna";
    private static final boolean DEBUG = true;


    public static final int SIGNATURE_ID_JPEG = 0;
    public static final int SIGNATURE_ID_PNG = 1;
    public static final int SIGNATURE_ID_GIF = 2;
    private static final String[] SIGNATURES = new String[3];

    static {
        SIGNATURES[0] = SIGNATURE_JPEG;
        SIGNATURES[1] = SIGNATURE_PNG;
        SIGNATURES[2] = SIGNATURE_GIF;
    }

    String curFileName;

    EditText edittext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileexplorer);
        edittext = (EditText) findViewById(R.id.editText);
    }

    public void getfile(View view) {
        Intent intent1 = new Intent(this, FileChooser.class);
        startActivityForResult(intent1, REQUEST_PATH);
    }

    // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See which child activity is calling us back.
        if (requestCode == REQUEST_PATH) if (resultCode == RESULT_OK) {
            curFileName = data.getStringExtra("GetPath");

            InputStream is = null;
            try {

                File f = new File(curFileName);
                is = new FileInputStream(f);

              ContentHandler contenthandler = new BodyContentHandler();
                Metadata metadata = new Metadata();
                metadata.set(Metadata.RESOURCE_NAME_KEY, f.getName());
                Parser parser = new AutoDetectParser();
                ParseContext parseContext= new ParseContext();
                // OOXMLParser parser = new OOXMLParser();
                parser.parse(is, contenthandler, metadata,parseContext);
                System.out.println("Mime: " + metadata.get(Metadata.CONTENT_TYPE));
                System.out.println("Title: " + metadata.get(Metadata.TITLE));
                System.out.println("Author: " + metadata.get(Metadata.AUTHOR));
                System.out.println("content: " + contenthandler.toString());
                Log.e(TAG,metadata.get(Metadata.CONTENT_TYPE));
                Tika tika= new Tika();

                MimeTypes mimeTypes= new MimeTypes();
                String typ= tika.detect(is);
               /* MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
                Collection mimeTypes = MimeUtil.getMimeTypes(f);*/
                //File fi = new File ("c:/temp/mime/test.doc");
                //File fi = new File ("c:/temp/mime/test.doc");
                // Collection<?> mimeTypes1 = MimeUtil.getMimeTypes(f);
                // System.out.println(mimeTypes1);
              /* System.out.println( typ);*/


//Krishna Working Mime Util Code
       /*         MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
  Collection mimeTypes1=MimeUtil.getMimeTypes(f);
  if (mimeTypes1.isEmpty()) {
    Log.e(TAG,"text/plain");
  }
 else {
    Iterator iterator=mimeTypes1.iterator();
    MimeType mimeType=(MimeType)iterator.next();
    String mimetype=mimeType.getMediaType() + "/" + mimeType.getSubType();
      Log.e(TAG,mimetype);
  }*/



                String p = getSignatureIdFromHeader(is);

                if (p.contains(FileSignatures.PDF.toLowerCase())) {
                    Toast.makeText(getApplicationContext(), "PDF", Toast.LENGTH_LONG).show();
                } else if (p.contains(FileSignatures.MS_EXE_FILE_.toLowerCase())) {
                    Toast.makeText(getApplicationContext(), "EXE", Toast.LENGTH_LONG).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static String getSignatureIdFromHeader(InputStream is) throws IOException {
        // read signature from head of source and compare with known signatures
        int signatureId = -1;
        int sigCount = SIGNATURES.length;
        int[] byteArray = new int[100];
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            byteArray[i] = is.read();
            builder.append(Integer.toHexString(byteArray[i]));
        }
        if (DEBUG) {
            Log.d(TAG, "head bytes=" + builder.toString());
        }

        return builder.toString();
    }
}
