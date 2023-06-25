package com.phage.ex_sepim.listtener;

import com.phage.ex_sepim.entity.SqlMessage;

import java.util.List;

public interface OnRecyclerItemClickListener {
    //RecyclerView的点击事件，将信息回调给view
    void onItemClick(int Position, List<SqlMessage> list);


}