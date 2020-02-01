package com.example.mylife.fragment.done;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.mylife.R;
import com.example.mylife.bean.TodoSection;
import com.example.mylife.utils.TimeUtil;

import java.util.Date;

import javax.inject.Inject;

public class DoneAdapter extends BaseSectionQuickAdapter<TodoSection,BaseViewHolder>{


    @Inject
    public DoneAdapter() {
        super( R.layout.item_done, R.layout.todo_item_head,null);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, TodoSection item) {
        helper.setText(R.id.todo_head, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, TodoSection item) {
        helper.setText(R.id.tv_id_title,item.t.getTitle());
        helper.setText(R.id.tv_id_des,item.t.getContent());
        helper.setText(R.id.tv_id_date, TimeUtil.format(new Date(item.t.getDate())));
        helper.addOnClickListener(R.id.iv_id_delete);
        helper.addOnClickListener(R.id.iv_id_no_complete);
    }

}