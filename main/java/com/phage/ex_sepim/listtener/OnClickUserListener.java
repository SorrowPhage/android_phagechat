package com.phage.ex_sepim.listtener;

import com.phage.ex_sepim.entity.User;

import java.util.List;

public interface OnClickUserListener {
    void onItemClick(int Position, List<User> list);
}
