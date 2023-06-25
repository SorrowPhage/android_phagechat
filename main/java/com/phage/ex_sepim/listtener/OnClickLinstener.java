package com.phage.ex_sepim.listtener;

import com.phage.ex_sepim.entity.Message;

import java.util.List;

public interface OnClickLinstener {
    void onItemClick(int Position, List<Message> list);
}
