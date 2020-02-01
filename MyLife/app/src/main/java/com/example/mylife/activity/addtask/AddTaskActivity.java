package com.example.mylife.activity.addtask;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mylife.R;
import com.example.mylife.base.BaseActivity;
import com.example.mylife.bean.TodoSection;
import com.example.mylife.bean.TodoTaskDetail;
import com.example.mylife.constant.Constant;
import com.example.mylife.utils.TimeUtil;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * des:新增待办的Activity
 */

public class AddTaskActivity extends BaseActivity<AddTaskActivityImp> implements AddTaskActivityContract.View {
    @BindView(R.id.et_adt_title)
    EditText etAdtTitle;
    @BindView(R.id.et_adt_content)
    EditText etAdtContent;
    @BindView(R.id.tv_adt_date)
    TextView tvAdtDate;
    @BindView(R.id.btn_adt_save)
    Button btnAdtSave;

    int mYear = 2018;
    int mMonth = 8;
    int mDay = 18;
    private String mCustomType = "0";
    private int mEditType = 0;
    private TodoSection todoSection;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_task;
    }

    @Override
    protected void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    protected void initView() {

        Bundle bundle = getIntent().getExtras();

        mYear= Calendar.getInstance().get(Calendar.YEAR);
        mMonth=  Calendar.getInstance().get(Calendar.MONTH)-1;
        mDay =  Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//
//        mYear=new Date().getYear();
//        mMonth=new Date().getMonth()-1;
//        mDay=new Date().getDay();


        if (bundle != null) {
            todoSection = (TodoSection) bundle.getSerializable( Constant.TASK_KEY);
            if (null != todoSection) {
                mToolbar.setTitle("待办详情");
                etAdtTitle.setText(todoSection.t.getTitle());
                etAdtContent.setText(todoSection.t.getContent());
                tvAdtDate.setText(todoSection.t.getDateStr());
                String format = TimeUtil.format(new Date(todoSection.t.getDate()));
                mYear= Integer.parseInt(format.split("-")[0]);
                mMonth= Integer.parseInt(format.split("-")[1]);
                mDay= Integer.parseInt(format.split("-")[2]);
            }
            btnAdtSave.setText("更新");

        } else {
            mToolbar.setTitle("新增待办");
            String format = TimeUtil.format(new Date(), TimeUtil.DEFAULT_PATTERN);
            mYear = Integer.parseInt(format.split("-")[0]);
            mMonth = Integer.parseInt(format.split("-")[1]);
            mDay = Integer.parseInt(format.split("-")[2]);
            tvAdtDate.setText(mYear + "-" + mMonth + "-" + mDay);
        }
    }

    @OnClick({R.id.tv_adt_date, R.id.btn_adt_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_adt_date:
                new DatePickerDialog(AddTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {




                        tvAdtDate.setText(year + "-" + month + "-" + dayOfMonth);

                    }
                }, mYear, mMonth-1, mDay).show();
                break;
            case R.id.btn_adt_save:
                showLoading();
                switch (btnAdtSave.getText().toString()) {
                    case "添加":
                        mPresenter.addTask(etAdtTitle.getText().toString(), etAdtContent.getText().toString(), tvAdtDate.getText().toString(), mCustomType);
                        break;
                    case "更新":
                        mPresenter.updateTask(todoSection.t.getId(), etAdtTitle.getText().toString(), etAdtContent.getText().toString(), tvAdtDate.getText().toString(), todoSection.t.getStatus(), mEditType);
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void showAddTaskSuccess(TodoTaskDetail.DatasBean data) {

        showSuccess("添加成功!");
        Intent intent = new Intent();
        intent.putExtra(Constant.ADD_DATA, data);
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public void showUpdateSuccess(TodoTaskDetail.DatasBean data) {
        showSuccess("更新成功!");
        Intent intent = new Intent();
        intent.putExtra(Constant.UPDATE_DATA, data);
        setResult(RESULT_OK, intent);
        finish();
    }

    // TODO 分类
    //    private void showBottomSheet() {
//        new BottomSheet.Builder(this, R.style.BottomSheet_StyleDialog)
//                .title("选择分类:")
//                .sheet(R.menu.add_todo_bottom_sheet)
//                .listener(new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case R.id.item_add_default:
//                                mCustomType = "0";
//                                tvTypeName.setText("默认");
//                                break;
//                            case R.id.item_add_work:
//                                mCustomType = "1";
//                                tvTypeName.setText("工作");
//                                break;
//                            case R.id.item_add_life:
//                                mCustomType = "2";
//                                tvTypeName.setText("生活");
//                                break;
//                            case R.id.item_add_study:
//                                mCustomType = "3";
//                                tvTypeName.setText("学习");
//                                break;
//                        }
//                    }
//                }).show();
//    }
}
