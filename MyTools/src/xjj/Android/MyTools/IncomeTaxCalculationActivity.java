package xjj.Android.MyTools;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IncomeTaxCalculationActivity extends Activity {

	private EditText editTextTotalIncome;
	private EditText editTextPreTaxDeduction;
	private EditText editTextIncomeTaxThreshold;
	private EditText editTextIncomeTax;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tax_calculation);
		
		editTextTotalIncome = (EditText)this.findViewById(R.id.editTextTotalIncome);
		editTextPreTaxDeduction = (EditText)this.findViewById(R.id.editTextPreTaxDeduction);
		editTextIncomeTaxThreshold = (EditText)this.findViewById(R.id.editTextIncomeTaxThreshold);
		editTextIncomeTax = (EditText)this.findViewById(R.id.editTextIncomeTax);
		
		//按钮：返回主界面
		Button ButtonReturn = (Button)this.findViewById(R.id.buttonReturn_Tax);
		ButtonReturn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(IncomeTaxCalculationActivity.this, MainActivity.class));
				IncomeTaxCalculationActivity.this.finish();
			}
		});	
		
		//按钮：清零收入总额
		Button ButtonCleanupIncome = (Button)this.findViewById(R.id.buttonCleanupIncome);
		ButtonCleanupIncome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editTextTotalIncome.setText("");
			}
		});
		
		//按钮：清零税前扣除
		Button ButtonCleanupDeduction = (Button)this.findViewById(R.id.ButtonCleanupDeduction);
		ButtonCleanupDeduction.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editTextPreTaxDeduction.setText("");
			}
		});
			
		//按钮：清零应缴税额
		Button ButtonCleanupIncomeTax = (Button)this.findViewById(R.id.ButtonCleanupIncomeTax);
		ButtonCleanupIncomeTax.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editTextIncomeTax.setText("");
			}
		});
			
		
		//按钮：计算
		Button buttonTaxCalculation = (Button)this.findViewById(R.id.buttonTaxCalculation);
		buttonTaxCalculation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				double totalIncome=0;
				double preTaxDeduction=0;
				double incomeTaxThreshold=0;
				double tax=0;
				double taxibleIncome;
				
				if(editTextTotalIncome.getText().toString().equals("")){
					new AlertDialog.Builder(IncomeTaxCalculationActivity.this)
				     .setTitle("有问题！")
				     .setMessage("请输入“收入总额”！")
				     .setPositiveButton("哦，是哦，马上输入！", null)
				     .show();
					return;
				}
				else{
					totalIncome = Double.parseDouble(editTextTotalIncome.getText().toString());
			    }
				
				if(editTextPreTaxDeduction.getText().toString().equals(""))
					preTaxDeduction = 0;
				else
					preTaxDeduction = Double.parseDouble(editTextPreTaxDeduction.getText().toString());
				
				if(editTextIncomeTaxThreshold.getText().toString().equals(""))
					incomeTaxThreshold = 0;
				else
					incomeTaxThreshold = Double.parseDouble(editTextIncomeTaxThreshold.getText().toString());
				
				taxibleIncome = totalIncome - preTaxDeduction - incomeTaxThreshold;
				
				if(taxibleIncome<=0){
					tax = 0;
				}
				else if (taxibleIncome<=1500){
					tax = taxibleIncome * 0.03;
				}
				else if (taxibleIncome<=4500){
					tax = taxibleIncome * 0.1 - 105;
				}
				else if (taxibleIncome<=9000){
					tax = taxibleIncome * 0.2 - 555;
				}
				else if (taxibleIncome<=35000){
					tax = taxibleIncome * 0.25 - 1005;
				}
				else if (taxibleIncome<=55000){
					tax = taxibleIncome * 0.3 - 2755;
				}
				else if (taxibleIncome<=80000){
					tax = taxibleIncome * 0.35 - 5505;
				}
				else
					tax = taxibleIncome * 0.45 - 13505;
				
				editTextIncomeTax.setText(Double.toString(tax));
				
			}//OnClick
		});
		
		
		//按钮：反推工资
		Button buttonCalculateIncome = (Button)this.findViewById(R.id.buttonCalculateIncome);
		buttonCalculateIncome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				double totalIncome=0;
				double preTaxDeduction=0;
				double incomeTaxThreshold=0;
				double tax=0;
				double taxibleIncome=0;
				
				if(editTextIncomeTax.getText().toString().equals("")){
					new AlertDialog.Builder(IncomeTaxCalculationActivity.this)
				     .setTitle("有问题！")
				     .setMessage("请输入“应缴税额”！")
				     .setPositiveButton("哦，没错，马上输入！", null)
				     .show();
					return;
				}
				else{
					tax = Double.parseDouble(editTextIncomeTax.getText().toString());
			    }
				
				if(editTextPreTaxDeduction.getText().toString().equals(""))
					preTaxDeduction = 0;
				else
					preTaxDeduction = Double.parseDouble(editTextPreTaxDeduction.getText().toString());
				
				if(editTextIncomeTaxThreshold.getText().toString().equals(""))
					incomeTaxThreshold = 0;
				else
					incomeTaxThreshold = Double.parseDouble(editTextIncomeTaxThreshold.getText().toString());
				
				
				
				if(tax<=0){
					taxibleIncome = 0;
				}
				else if (tax<=105){
					taxibleIncome = tax/0.03;
				}
				else if (tax<=555){
					taxibleIncome = (tax + 105)/0.1;
				}
				else if (tax<=1005){
					taxibleIncome = (tax + 555)/0.2;
				}
				else if (tax<=2755){
					taxibleIncome = (tax + 1005)/0.25;
				}
				else if (tax<=5505){
					taxibleIncome = (tax + 2755)/0.3;
				}
				else if (tax<=13505){
					taxibleIncome = (tax + 5505)/0.35;
				}
				else
					taxibleIncome = (tax +13505)/0.45;

				totalIncome = taxibleIncome + preTaxDeduction + incomeTaxThreshold;
				
				if(tax<=0){
					new AlertDialog.Builder(IncomeTaxCalculationActivity.this)
				     .setTitle("不是吧？")
				     .setMessage("应缴税是零？那么你的工资应该是少于" + Double.toString(totalIncome))
				     .setPositiveButton("哦，好吧！", null)
				     .show();
				}
				else
					editTextTotalIncome.setText(Double.toString(totalIncome));
				
				
			}//OnClick
		});
		
		
	}//onCreate
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_income_tax_calculation, menu);
		return true;
	}

}
