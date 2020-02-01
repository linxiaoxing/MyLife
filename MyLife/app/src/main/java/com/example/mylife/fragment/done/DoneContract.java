package com.example.mylife.fragment.done;

import com.example.mylife.base.BaseContract;
import com.example.mylife.bean.TodoTaskDetail;

public class DoneContract {
    interface View extends BaseContract.BaseView{
        void showDoneTask(TodoTaskDetail data, int loadType);


        /**
         * 删除成功信息展示
         * @param message
         */
        void showDeleteSuccess(String message);

        /**
         * 标识完成的方法
         * @param message
         */
        void showMarkUnComplete(String message);


    }

    interface Presenter extends BaseContract.BasePresenter<View>{

        /**
         * 获取待办任务的数据
         * @param type 0,1,2,3
         */
        void getTodoList(int type);

        /**
         * 刷新方法
         */
        void refresh();


        /**
         * 删除一条todo任务
         * @param id
         */
        void deleteTodo(int id);

        /**
         * 仅仅更新一条状态
         * @param id
         */
        void updataStatus(int id);

        /**
         * 加载更多的方法
         */
        void loadMore();

    }

}