package find.listview.control;

import com.example.dp2.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/*
public class FindListViewAdapter extends Activity{
	private ListView listview;
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_test);
 
        listview = (ListView) this.findViewById(R.id.listview);
 
        // 设置适配器的图片资源
        int[] imageId = new int[] { R.drawable.chat_tool_camera,
                R.drawable.chat_tool_location, R.drawable.chat_tool_paint,
                R.drawable.chat_tool_video, R.drawable.chat_tool_voice,
                R.drawable.about_brand };
 
        // 设置标题
        String[] title = new String[] { 相机, 定位, 画笔, 视频, 声音, 聊天 };
        List<map<string, object="">> listitem = new ArrayList<map<string, object="">>();
 
        // 将上述资源转化为list集合
        for (int i = 0; i < title.length; i++) {
            Map<string, object=""> map = new HashMap<string, object="">();
            map.put(image, imageId[i]);
            map.put(title, title[i]);
 
            listitem.add(map);
        }
 
        ListViewAdapter adapter = new ListViewAdapter(this, listitem);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<!--?--> parent, View view,
                    int position, long id) {
                Toast.makeText(ListViewUseAdapter.this, haha, Toast.LENGTH_SHORT).show(); 
            }
        });
    }
}
*/
