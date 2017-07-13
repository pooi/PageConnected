package ga.pageconnected.pageconnected.util.pdf;

import android.content.Context;
import android.os.Looper;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import ga.pageconnected.pageconnected.R;

public abstract class GeneratePDF extends Thread{

	private DialogListener listener;
	private Context context;
	
	private ArrayList<HashMap<String, Object>> list;
	private int padding = 10;
	private OutputStream stream;
	private boolean check;

	public GeneratePDF(Context context){
		this.context = context;
	}
	public GeneratePDF(Context context, OutputStream stream, HashMap<String, Object> data, DialogListener listener){
		this(context);
		this.stream = stream;
		list = new ArrayList<>();
		list.add(data);
		this.listener = listener;
	}
	public GeneratePDF(Context context, OutputStream stream, ArrayList<HashMap<String, Object>> list, DialogListener listener){
		this(context);
		this.stream = stream;
		this.list = list;
		this.listener = listener;
	}

	protected abstract void afterThreadFinish(boolean status);
	
	public void run(){

		check = false;
		
		try{
			
			Document document = new Document(PageSize.A4, padding, padding, padding, padding);
			PdfWriter writer = PdfWriter.getInstance(document, stream);

			if(listener != null){
				listener.changeContent(R.string.loading_font_file);
			}
			BaseFont objBaseFont = BaseFont.createFont("assets/NanumGothic.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			
			document.open();
			
			for(int i=0; i<list.size(); i++){
				int layout = (int)list.get(i).get("layout");
				if(listener != null){
					listener.changeContent(String.format(context.getResources().getString(R.string.generating_pdf_page), i, list.size()));
				}
				switch(layout){
				case 0:
					CreateLayout01 layout01 = new CreateLayout01(list.get(i), objBaseFont);
					layout01.create(document, writer);
					break;
				case 1:
					CreateLayout02 layout02 = new CreateLayout02(list.get(i), objBaseFont);
					layout02.create(document, writer);
					break;
				case 2:
					CreateLayout03 layout03 = new CreateLayout03(list.get(i), objBaseFont);
					layout03.create(document, writer);
					break;
				case 3:
					CreateLayout04 layout04 = new CreateLayout04(list.get(i), objBaseFont);
					layout04.create(document, writer);
					break;
				case 4:
					CreateLayout05 layout05 = new CreateLayout05(list.get(i), objBaseFont);
					layout05.create(document, writer);
					break;
				}
			}
			
			document.close();
			
			System.out.println("Done!");

			check = true;
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			Looper.prepare();
			afterThreadFinish(check);
		}
		
	}
	
	

}
