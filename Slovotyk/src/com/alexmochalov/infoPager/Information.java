package com.alexmochalov.infoPager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import com.alexmochalov.main.MainActivity;
import com.alexmochalov.main.Utils;
import com.alexmochalov.slovotyk.R;

public class Information {

	
	/**
	 * Вывести ViewPager с информацией о приложении
	 */
	 static public void showInformation(final MainActivity context) {
		Utils.hideActionBar();
		
		LayoutInflater inflater = LayoutInflater.from(context);
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
        textView1.setText(context.getResources().getText(R.string.info_about));        
        textView2.setText(Html.fromHtml(context.getResources().getText(R.string.info_about1).toString()));        
        pages.add(page);

        page = inflater.inflate(R.layout.page1, null);
        textView1 = (TextView) page.findViewById(R.id.textViewAbout1);
        textView2 = (TextView) page.findViewById(R.id.textViewAbout2);
        imageView = (ImageView) page.findViewById(R.id.imageViewAbout);
        imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.info1));
        textView1.setText(context.getResources().getText(R.string.info_loading));        
        textView2.setText(Html.fromHtml(context.getResources().getText(R.string.info_loading1).toString()));        
        pages.add(page);

        page = inflater.inflate(R.layout.page1, null);
        textView1 = (TextView) page.findViewById(R.id.textViewAbout1);
        textView2 = (TextView) page.findViewById(R.id.textViewAbout2);
        imageView = (ImageView) page.findViewById(R.id.imageViewAbout);
        imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.info2));
        textView1.setText(context.getResources().getText(R.string.info_marking));        
        textView2.setText(Html.fromHtml(context.getResources().getText(R.string.info_marking1).toString()));        
        pages.add(page);

        page = inflater.inflate(R.layout.page1, null);
        textView1 = (TextView) page.findViewById(R.id.textViewAbout1);
        textView2 = (TextView) page.findViewById(R.id.textViewAbout2);
        imageView = (ImageView) page.findViewById(R.id.imageViewAbout);
        imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.info3));
        textView1.setText(context.getResources().getText(R.string.info_list));        
        textView2.setText(Html.fromHtml(context.getResources().getText(R.string.info_list1).toString()));        
        pages.add(page);

        page = inflater.inflate(R.layout.page1, null);
        textView1 = (TextView) page.findViewById(R.id.textViewAbout1);
        textView2 = (TextView) page.findViewById(R.id.textViewAbout2);
        imageView = (ImageView) page.findViewById(R.id.imageViewAbout);
        imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.info4));
        textView1.setText(context.getResources().getText(R.string.info_dict));         
        textView2.setText(Html.fromHtml(context.getResources().getText(R.string.info_dict1).toString()));        
        pages.add(page);
        
        context.setContentView(R.layout.page_about);
        final ViewPager viewPager = (ViewPager)context.findViewById(R.id.viewpager);
        final RadioGroup radioGroup = (RadioGroup)context.findViewById(R.id.radiogroup);
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
        
        Button ButtonMail = (Button)context.findViewById(R.id.imageButtonMail);
        ButtonMail.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
			            "mailto","maaventrade@gmail.com", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Slovotyk Application");
				context.startActivity(Intent.createChooser(emailIntent, "Send Email..."));				
			}
        });
        
        Button ButtonOk = (Button)context.findViewById(R.id.ButtonOk);
        ButtonOk.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// Close the informational window
				context.onBackPressed();
			}
        });
        
        Button ButtonSample = (Button)context.findViewById(R.id.ButtonSample);
        ButtonSample.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// Close the informational window and load sample file
				context.onBackPressed();
				// Start loading the text 
				context.loadFile(Utils.getSampleFileName(), true);
				 
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
