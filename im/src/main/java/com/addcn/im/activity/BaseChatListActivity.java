
package com.addcn.im.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addcn.im.R;
import com.addcn.im.adapter.ImListAdapter;
import com.addcn.im.config.Config;
import com.addcn.im.interfaces.OnResultCallbackListener;
import com.addcn.im.request.ApiRequestHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wyq.fast.utils.ObjectValueUtil;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Author:YANG Bin
 * Chat Activity
 */
public abstract class BaseChatListActivity extends AppCompatActivity implements View.OnClickListener,
        OnLoadMoreListener {
    private OnItemClickListener mListener;
    private String mSelectItemUid = "";
    private String mCurToken = "";
    private String mTargetUid = "";
    private String mTargetName = "";
    private final String[] mLongClickTitle = {"刪除聊天"};//列表页删除，文字
    private String mNickname = "";
    private static final String UPDATEIMMESSAGE_ACTION = Config.updateimmessageAction;
    //地址监听
    private UpdataImMessageReceiver updataImMessageReceiver;
    private List mList = new ArrayList();
    private ImageView mBtBack;
    //页码
    private int mPageIndex = 1;
    /**
     * 下拉刷新
     **/
    private SmartRefreshLayout mRefreshLayout;
    public ImListAdapter mImListAdapter;
    private RecyclerView mHouseListRecy;
    private LinearLayoutManager layoutManage;
    private boolean mIsRefresh = true;//当前操作是否是下拉刷新
    private LinearLayout mLvNull;//无数据布局展示
    private LinearLayout mLoading;//loading


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_question_answer);
        initData();
        initView();
        //监听，消息来了请求 刷新列表变化
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATEIMMESSAGE_ACTION);
        updataImMessageReceiver = new UpdataImMessageReceiver();
        registerReceiver(updataImMessageReceiver, filter);

    }

    private void initData() {
        mCurToken = ObjectValueUtil.getString(getIntent().getExtras(), "token");
        mTargetUid = ObjectValueUtil.getString(getIntent().getExtras(), "target_uid");
        mTargetName = ObjectValueUtil.getString(getIntent().getExtras(), "target_name");
    }


    /**
     * 初始化
     */
    private void initView() {

        mLoading = (LinearLayout) findViewById(R.id.ll_first_load);
        mBtBack = (ImageView) findViewById(R.id.bt_back);
        mBtBack.setOnClickListener(this);
        mLvNull = (LinearLayout) findViewById(R.id.lv_null);
        mHouseListRecy = (RecyclerView) findViewById(R.id.rv_price);
        layoutManage = new LinearLayoutManager(this);
        layoutManage.setOrientation(LinearLayoutManager.VERTICAL);
        mHouseListRecy.setLayoutManager(layoutManage);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mImListAdapter = new ImListAdapter(R.layout.item_im_list, mList, this);
        mHouseListRecy.setAdapter(mImListAdapter);
        setAdapterItemLongClick();
        setItemClick();

        //自动加载
        mImListAdapter.getLoadMoreModule().setOnLoadMoreListener(this);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mIsRefresh = true;
                mPageIndex = 1;
                loadData(true);
            }
        });
        loadData(true);
    }


    /**
     * 长按点击事件
     */
    private void setAdapterItemLongClick() {
        mImListAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

                if (mImListAdapter != null) {
                    mNickname = mImListAdapter.getData().get(position).get("nickname");
                    mSelectItemUid = mImListAdapter.getData().get(position).get("uid");
                }
                new AlertDialog.Builder(BaseChatListActivity.this)
                        .setTitle(mNickname)
                        .setItems(mLongClickTitle, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                new AlertDialog.Builder(
                                        BaseChatListActivity.this)
                                        .setTitle("確定刪除")
                                        .setMessage("聊天記錄一旦被刪除將無法恢復！是否刪除聊天記錄？")
                                        .setPositiveButton(
                                                "是",
                                                new DialogInterface.OnClickListener() {

                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {
                                                        setDeleteIm(mSelectItemUid);
                                                    }
                                                })
                                        .setNegativeButton(
                                                "否",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {
                                                    }
                                                }).show();
                            }
                        }).show();


                return true;
            }

        });
    }


    protected abstract void setAdapterItemClick(String target_uid, String target_name, String token, int postion);

    /**
     * 拿到传入的url来做长按删除 接口
     *
     * @return
     */
    protected abstract String initSetDeleteIm();

    /**
     * 拿到传入的url来做长按删除 接口
     *
     * @return
     */
    protected abstract String initListData();

    /**
     * 点击itme跳转去聊天页面
     */
    private void setItemClick() {
        mImListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                List<HashMap<String, String>> data = (List<HashMap<String, String>>) adapter.getData();
                setAdapterItemClick(data.get(position).get("uid"), data.get(position).get("nickname"), mCurToken, position);
            }
        });
    }


    /**
     * 删除聊天記錄
     *
     * @param id 被删除用户的ID
     */
    private void setDeleteIm(String id) {
        HashMap<String, String> map = new HashMap();
        map.put("target_uid", id);
        map.put("token", mCurToken);

        ApiRequestHelper.Companion.getInstance().doPost(initSetDeleteIm(), map, new OnResultCallbackListener() {
            @Override
            public void onCallbackListener(@Nullable String string) {
                try {
                    JSONObject json = ObjectValueUtil.getJSONObject(string);
                    String code = json.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(BaseChatListActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
                        loadData(true);
                    }
                } catch (Exception e) {
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_back) {
            goBack();
        }
    }

    /**
     * 返回按钮
     */
    private void goBack() {
        finish();

    }

    /**
     * 加载数据
     */
    private void loadData(boolean isRefresh) {
        //加載數據
        if (isRefresh) {
            mLoading.setVisibility(View.GONE);
            mPageIndex = 1;
        }
        Map map = new HashMap();
        map.put("token", mCurToken);
        map.put("limit", "15");
        map.put("page", mPageIndex + "");

        ApiRequestHelper.Companion.getInstance().doGet(initListData() + mCurToken + "&limit=15&page=" + mPageIndex, new OnResultCallbackListener() {
            @Override
            public void onCallbackListener(@Nullable String string) {
                try {
                    mLoading.setVisibility(View.GONE);
                    JSONObject json = ObjectValueUtil.getJSONObject(string);
                    String code = json.getString("code");
                    if (("200").equals(code)) {
                        mRefreshLayout.setVisibility(View.VISIBLE);
                        mLvNull.setVisibility(View.GONE);
                        JSONArray data = ObjectValueUtil.getJSONArray(json, "data");
                        if (data != null && data.length() > 0) {
                            List<HashMap<String, String>> list1 = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject item = ObjectValueUtil.getJSONObject(data, i);
                                HashMap<String, String> houseData = new HashMap<String, String>();
                                houseData.put("uid", ObjectValueUtil.getJSONValue(item, "uid"));
                                houseData.put("nickname", ObjectValueUtil.getJSONValue(item, "nickname"));
                                houseData.put("avatar", ObjectValueUtil.getJSONValue(item, "avatar"));
                                houseData.put("new_msg_count", ObjectValueUtil.getJSONValue(item, "new_msg_count"));
                                houseData.put("last_time", ObjectValueUtil.getJSONValue(item, "last_time"));
                                houseData.put("type", ObjectValueUtil.getJSONValue(item, "type"));

                                String string_content = ObjectValueUtil.getJSONValue(item, "content");
                                if (!string_content.equals("")) {
                                    JSONObject content = (JSONObject) item.get("content");
                                    if (content.length() > 0) {
                                        String img_url = ObjectValueUtil.getJSONValue(content, "img_url");
                                        String thumbnail_url = ObjectValueUtil.getJSONValue(content, "thumbnail_url");
                                        String extra = ObjectValueUtil.getJSONValue(content, "extra");
                                        String last_content = ObjectValueUtil.getJSONValue(content, "content");
                                        String title = ObjectValueUtil.getJSONValue(content, "title");
                                        houseData.put("img_url", img_url);
                                        houseData.put("thumbnail_url", thumbnail_url);
                                        houseData.put("extra", extra);
                                        houseData.put("last_content", last_content);
                                        houseData.put("title", title);

                                    } else {
                                        houseData.put("content", "");
                                    }
                                } else {
                                    houseData.put("content", ObjectValueUtil.getJSONValue(item, "content"));
                                }
                                mImListAdapter.addData(houseData);
                                list1.add(houseData);
                            }
                            if (isRefresh) {
                                //结束下拉刷新动画
                                mRefreshLayout.finishRefresh();
                                mImListAdapter.setNewData(list1);
                                mImListAdapter.getLoadMoreModule().loadMoreComplete();
                                if (mPageIndex == 1) {
                                    mHouseListRecy.scrollToPosition(0);
                                }
                            } else {
                                //结束加载更多动画
                                mImListAdapter.getLoadMoreModule().loadMoreComplete();
                            }

                        } else {
                            //0筆物件
                            if (mPageIndex == 1) {
                                mLvNull.setVisibility(View.VISIBLE);
                                mRefreshLayout.setVisibility(View.GONE);
                            }
                            mImListAdapter.getLoadMoreModule().loadMoreEnd(true);
                        }

                    }
                } catch (Exception e) {
                }

            }
        });
    }

    @Override
    public void onLoadMore() {
        mIsRefresh = false;
        mPageIndex = mPageIndex + 1;
        loadData(mIsRefresh);
    }


    /**
     * 廣播接收
     */
    public class UpdataImMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context content, Intent intent) {
            if (intent != null) {
                loadData(true);
            }
        }

    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 0x002 && resultCode == 0x001) {
//            if (BaseApplication.getInstance().isHouseUserLogin()) {
//                String isSynchronization = sharedPreferencesUtil.getString("isSynchronization");
//                String is_send_im = sharedPreferencesUtil.getString("isSendIm");
//                if (isSynchronization.equals("0") && is_send_im.equals("1")) {
////                    showDialog();
//                }
//            }
//        } else if (requestCode == 0x002 && resultCode == 0x002) {
//            pageIndex = 1;
//            loadData(true);
//            imListAdapter.notifyDataSetChanged();
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updataImMessageReceiver != null) {
            unregisterReceiver(updataImMessageReceiver);
        }
    }
}
