package ga.pageconnected.pageconnected.util;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.widget.ImageView;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import net.sf.andpdf.nio.ByteBuffer;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by tw on 2017. 7. 19..
 */

public class LoadBitmap extends Thread {

    private Handler uiHandler;
    private ImageView imageView;
    private String fileName;

    public LoadBitmap(ImageView imageView, String fileName){

        uiHandler = new Handler();
        this.imageView = imageView;
        this.fileName = fileName;

    }

    @Override
    public void run(){

        byte[] bytes;
        try{

            File dir = new File(Environment.getExternalStorageDirectory(), "PageConnected/mymagazine");
            File file = new File(dir, fileName);
            FileInputStream is = new FileInputStream(file);

            // Get the size of the file
            long length = file.length();
            bytes = new byte[(int) length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
            ByteBuffer buffer = ByteBuffer.NEW(bytes);
//            String data = Base64.encodeToString(bytes, Base64.DEFAULT);
            PDFFile pdf_file = new PDFFile(buffer);
            PDFPage page = pdf_file.getPage(0, true);

//            Thread.sleep(2000);

            RectF rect = new RectF(0, 0, (int) page.getBBox().width(),
                    (int) page.getBBox().height());

            final Bitmap bitmap = page.getImage((int)rect.width(), (int)rect.height(), rect);

            uiHandler.post(new Runnable() {
                public void run(){
                    imageView.setImageBitmap(bitmap);
                }
            });

        }catch (Exception e){
            System.out.println("Load Bitmap : " + e.getMessage());
        }

    }

    private Bitmap getBitmap(String fileName){

        byte[] bytes;
        try {

            File dir = new File(Environment.getExternalStorageDirectory(), "PageConnected/mymagazine");
            File file = new File(dir, fileName);
            FileInputStream is = new FileInputStream(file);

            // Get the size of the file
            long length = file.length();
            bytes = new byte[(int) length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
            ByteBuffer buffer = ByteBuffer.NEW(bytes);
            String data = Base64.encodeToString(bytes, Base64.DEFAULT);
            PDFFile pdf_file = new PDFFile(buffer);
            PDFPage page = pdf_file.getPage(0, true);

            RectF rect = new RectF(0, 0, (int) page.getBBox().width(),
                    (int) page.getBBox().height());

            Bitmap image = page.getImage((int)rect.width(), (int)rect.height(), rect);
//            File file1 = new File(dir, "sixth.jpg");
//            FileOutputStream os = new FileOutputStream(file1);
//            image.compress(Bitmap.CompressFormat.PNG, 100, os);
            //((ImageView) findViewById(R.id.testView)).setImageBitmap(image);
            return image;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
