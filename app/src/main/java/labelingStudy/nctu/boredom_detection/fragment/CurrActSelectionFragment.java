package labelingStudy.nctu.boredom_detection.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.dao.ImageDataRecordDAO;
import labelingStudy.nctu.minuku.logger.Log;

public class CurrActSelectionFragment extends BottomSheetDialogFragment {
    private View view;
    private CheckBox act1,act2,act3,act4,act5,act6,act7,act8,act9,act10,act11,act12,act13,act14,act15,act16,act17,act18,act19,act20,act21;
    private Button button_confirm;//, button_cancel;
    private EditText others_detail;
    private long[] idArrays;
    String act_str ="";

    String TAG = "CurrActSelectionFragment";

    private ImageDataRecordDAO ImageDataRecordDAO;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageDataRecordDAO  =  appDatabase.getDatabase(getActivity().getApplicationContext()).imageDataRecordDAO();

        Bundle bundle = getArguments();
        if (bundle != null) {
            idArrays = bundle.getLongArray("id_array");
        }

        Log.d(TAG, "test"  + Arrays.toString(idArrays));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_curr_act, container, false);
        button_confirm = (Button) view.findViewById(R.id.act_confirm);
        //button_cancel = (Button) view.findViewById(R.id.act_cancel);
        act1 = (CheckBox) view.findViewById(R.id.act1);
        act2 = (CheckBox) view.findViewById(R.id.act2);
        act3 = (CheckBox) view.findViewById(R.id.act3);
        act4 = (CheckBox) view.findViewById(R.id.act4);
        act5 = (CheckBox) view.findViewById(R.id.act5);
        act6 = (CheckBox) view.findViewById(R.id.act6);
        act7 = (CheckBox) view.findViewById(R.id.act7);
        act8 = (CheckBox) view.findViewById(R.id.act8);
        act9 = (CheckBox) view.findViewById(R.id.act9);
        act10 = (CheckBox) view.findViewById(R.id.act10);
        act11 = (CheckBox) view.findViewById(R.id.act11);
        act12 = (CheckBox) view.findViewById(R.id.act12);
        act13 = (CheckBox) view.findViewById(R.id.act13);
        act14 = (CheckBox) view.findViewById(R.id.act14);
        act15 = (CheckBox) view.findViewById(R.id.act15);
        act16 = (CheckBox) view.findViewById(R.id.act16);
        act17 = (CheckBox) view.findViewById(R.id.act17);
        act18 = (CheckBox) view.findViewById(R.id.act18);
        act19 = (CheckBox) view.findViewById(R.id.act19);
        act20 = (CheckBox) view.findViewById(R.id.act20);
        act21 = (CheckBox) view.findViewById(R.id.act21);
        others_detail = (EditText)view.findViewById(R.id.act21_detail) ;

        others_detail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                act21.setChecked(true);
                act21.invalidate();
            }
        });

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> curr_act_list = new ArrayList<String>();
                if(act1.isChecked()){
                    curr_act_list.add("運動");
                }
                if(act2.isChecked()){
                    curr_act_list.add("吃飯");
                }
                if(act3.isChecked()){
                    curr_act_list.add("玩遊戲");
                }
                if(act4.isChecked()){
                    curr_act_list.add("讀書");
                }
                if(act5.isChecked()){
                    curr_act_list.add("無聊放空");
                }
                if(act6.isChecked()){
                    curr_act_list.add("睡覺、休息");
                }
                if(act7.isChecked()){
                    curr_act_list.add("上廁所");
                }
                if(act8.isChecked()){
                    curr_act_list.add("洗澡");
                }
                if(act9.isChecked()){
                    curr_act_list.add("看電視、影片");
                }
                if(act10.isChecked()){
                    curr_act_list.add("上網");
                }
                if(act11.isChecked()){
                    curr_act_list.add("開會");
                }
                if(act12.isChecked()){
                    curr_act_list.add("工作、做正事");
                }
                if(act13.isChecked()){
                    curr_act_list.add("上課");
                }
                if(act14.isChecked()){
                    curr_act_list.add("開車、騎機車");
                }
                if(act15.isChecked()){
                    curr_act_list.add("通勤、移動中");
                }
                if(act16.isChecked()){
                    curr_act_list.add("談話");
                }
                if(act17.isChecked()){
                    curr_act_list.add("打電話");
                }
                if(act18.isChecked()){
                    curr_act_list.add("使用通訊類型APP");
                }
                if(act19.isChecked()){
                    curr_act_list.add("使用非通訊類型APP");
                }
                if(act20.isChecked()){
                    curr_act_list.add("忘記了");
                }
                if(act21.isChecked()){
                    if (others_detail.getText().toString().length()>0)
                    {
                        curr_act_list.add(others_detail.getText().toString());
                    }else {
                        curr_act_list.add("其他");
                    }
                }
                if(curr_act_list.isEmpty())
                {
                    Toast.makeText(getActivity(), "選項不可以為空，請至少選擇一項，謝謝。", Toast.LENGTH_LONG).show();

                }else {
                    act_str = String.join(";", curr_act_list);
                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    //viewPosition = adapter.viewposition;
                                    for (long id : idArrays) {
                                        ImageDataRecordDAO.updateDoingById(id, act_str);
                                    }

                                }
                            }
                    ).start();

                    dismiss();
                }
            }
        });
        return view;
    }
}
