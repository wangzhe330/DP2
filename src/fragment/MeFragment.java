package fragment;

import com.example.dp2.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MeFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.me_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		((TextView)getView().findViewById(R.id.tvTop)).setText("old server ip : "+ FindFragment.SERVER_IP);
		
		View view = getView();
		
		final EditText newIPEditText = (EditText) view.findViewById(R.id.me_newip);
		
		Button btnSetIP = (Button)view.findViewById(R.id.me_btnsetip);
		btnSetIP.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String newIPString = newIPEditText.getText().toString();
				FindFragment.SERVER_IP = newIPString;
				((TextView)getView().findViewById(R.id.tvTop)).setText("new server ip : "+ FindFragment.SERVER_IP);
			}
			
			
		});
	}
}
