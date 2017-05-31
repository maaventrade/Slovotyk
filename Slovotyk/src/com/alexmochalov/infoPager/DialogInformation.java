package com.alexmochalov.infoPager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.alexmochalov.dic.Entry;
import com.alexmochalov.main.MainActivity;
import com.alexmochalov.main.Utils;
import com.alexmochalov.slovotyk.R;

public class DialogInformation  extends Dialog{
	
	private Activity mContext;
	private Dialog mDialog;
	public OnEventListener listener;
	
	public interface OnEventListener{
		public void onButtonLoadFile(String fileName, boolean fromRresource);
	}
	
	public DialogInformation(Activity context) {
		super(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen); // 
		mContext = context;
		mDialog = this;

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		Utils.hideActionBar();
		
		LayoutInflater inflater = LayoutInflater.from(mContext);
        List<View> pages = new ArrayList<View>();

		// Main screen contains text. 
        View page = inflater.inflate(R.layout.page1, null);
        TextView textView1 = (TextView) page.findViewById(R.id.textViewAbout1);
        TextView textView2 = (TextView) page.findViewById(R.id.textViewAbout2);
        ImageView imageView = (ImageView) page.findViewById(R.id.imageViewAbout);
        imageView.setImageBitmap(null);
        imageView.requestLayout();
        imageView.getLayoutParams().height = 20;
        
        imageView.setVisibility(View.INVISIBLE);
        textView1.setText(mContext.getResources().getText(R.string.info_about));        
        textView2.setText(Html.fromHtml(mContext.getResources().getText(R.string.info_about1).toString()));        
        pages.add(page);

        page = inflater.inflate(R.layout.page1, null);
        textView1 = (TextView) page.findViewById(R.id.textViewAbout1);
        textView2 = (TextView) page.findViewById(R.id.textViewAbout2);
        imageView = (ImageView) page.findViewById(R.id.imageViewAbout);
        imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.info1));
        textView1.setText(mContext.getResources().getText(R.string.info_loading));        
        textView2.setText(Html.fromHtml(mContext.getResources().getText(R.string.info_loading1).toString()));        
        pages.add(page);

        page = inflater.inflate(R.layout.page1, null);
        textView1 = (TextView) page.findViewById(R.id.textViewAbout1);
        textView2 = (TextView) page.findViewById(R.id.textViewAbout2);
        imageView = (ImageView) page.findViewById(R.id.imageViewAbout);
        imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.info2));
        textView1.setText(mContext.getResources().getText(R.string.info_marking));        
        textView2.setText(Html.fromHtml(mContext.getResources().getText(R.string.info_marking1).toString()));        
        pages.add(page);

        page = inflater.inflate(R.layout.page1, null);
        textView1 = (TextView) page.findViewById(R.id.textViewAbout1);
        textView2 = (TextView) page.findViewById(R.id.textViewAbout2);
        imageView = (ImageView) page.findViewById(R.id.imageViewAbout);
        imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.info3));
        textView1.setText(mContext.getResources().getText(R.string.info_list));        
        textView2.setText(Html.fromHtml(mContext.getResources().getText(R.string.info_list1).toString()));        
        pages.add(page);

        page = inflater.inflate(R.layout.page1, null);
        textView1 = (TextView) page.findViewById(R.id.textViewAbout1);
        textView2 = (TextView) page.findViewById(R.id.textViewAbout2);
        imageView = (ImageView) page.findViewById(R.id.imageViewAbout);
        imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.info4));
        textView1.setText(mContext.getResources().getText(R.string.info_dict));         
        textView2.setText(Html.fromHtml(mContext.getResources().getText(R.string.info_dict1).toString()));        
        pages.add(page);
        
        setContentView(R.layout.page_about);
        
        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                case R.id.radioButton1:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.radioButton2:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.radioButton3:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.radioButton4:
                    viewPager.setCurrentItem(3);
                    break;
                case R.id.radioButton5:
                    viewPager.setCurrentItem(4);
                    break;
                } 
			}});
        
        Button ButtonMail = (Button)findViewById(R.id.imageButtonMail);
        ButtonMail.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
			            "mailto","maaventrade@gmail.com", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Slovotyk Application");
				mContext.startActivity(Intent.createChooser(emailIntent, "Send Email..."));				
			}
        });
        
        Button ButtonOk = (Button)findViewById(R.id.ButtonOk);
        ButtonOk.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
        });
        
        Button ButtonSample = (Button)findViewById(R.id.ButtonSample);
        ButtonSample.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
				if (listener != null)
					listener.onButtonLoadFile(Utils.getSampleFileName(), true);
				 
			}
        });
        
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                case 0:
                    radioGroup.check(R.id.radioButton1);
                    break;
                case 1:
                    radioGroup.check(R.id.radioButton2);
                    break;
                case 2:
                    radioGroup.check(R.id.radioButton3);
                    break;
                case 3:
                    radioGroup.check(R.id.radioButton4);
                    break;
                case 4:
                    radioGroup.check(R.id.radioButton5);
                    break;
            }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        }); 
        
        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(pages);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
	}
	
}
