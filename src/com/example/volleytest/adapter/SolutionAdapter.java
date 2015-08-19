package com.example.volleytest.adapter;

import java.util.ArrayList;

import com.example.dp2.R;

import Cinema.CinemaInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SolutionAdapter extends BaseAdapter {

	ArrayList<ArrayList<CinemaInfo>> allSolutionList;
	Context ctx;
	private int oneRequestCnt;
	private ViewHolder viewHolder;
	
	public SolutionAdapter(Context ctx, ArrayList<ArrayList<CinemaInfo>> allSolutionList, int oneRequestCnt)
	{
		this.ctx = ctx;
		this.allSolutionList = allSolutionList;
		this.oneRequestCnt = oneRequestCnt;
		//Log.d("wzdebug", "in adapter, allSolution.szie() = " + this.allSolutionList.size());
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return allSolutionList.size();
	}

	@Override
	public ArrayList<CinemaInfo> getItem(int arg0) {
		// TODO Auto-generated method stub
		return allSolutionList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(ctx).inflate(R.layout.plan_solution_lits_item, null);
		if(convertView != null){
			/*if(viewHolder == null)
			{
				viewHolder = new ViewHolder();
				viewHolder.solution = (TextView)convertView.findViewById(R.id.solution_text);
			}*/
			TextView textView = (TextView)convertView.findViewById(R.id.solution_text);
			String textString = "";
			int i = 0;
			for(i=0; i<oneRequestCnt; i++)
			{
				textString += allSolutionList.get(position).get(i).getName();
			}
			textView.setText(textString);
			return convertView;
		}else{
			return convertView;
		}
	}
	
	private class ViewHolder
	{
		TextView solution;
	}
}
